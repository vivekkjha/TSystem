package com.vivek.tsystem.mvparch;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vivek.tsystem.common.utils.LogUtil;


/**
 * Created by vivek on 04/08/17.
 *
 * This implements the {@link Presenter} interface. This Presenter can interact with more than one
 * {@link MvpView}, based on the constructor used to create it. The most common use case for
 * have multiple {@link MvpView} controlled by the same presenter is an Activity with tabs,
 * where each view in the tab (typically a {@link android.app.Fragment}) is an {@link
 * MvpView}.
 * <p/>
 * It requests the model to load its initial data, it listens to events from the {@link
 * MvpView}(s) and passes the user actions on to the Model, then it updates the {@link
 * MvpView}(s) once the Model has completed its data update.
 */

public class PresenterImpl implements Presenter, MvpView.UserActionListener {

    private static final String TAG = PresenterImpl.class.getSimpleName();

    /**
     * The UI that this Presenter controls.
     */
    @Nullable
    private MvpView[] mUpdatableViews;

    /**
     * The Model that this Presenter controls.
     */
    private Model mModel;

    /**
     * The queries to load when the {@link android.app.Activity} loading this {@link
     * android.app.Fragment} is created.
     */
    private IMvpQuery[] mInitialQueriesToLoad;

    /**
     * The actions allowed by the presenter.
     */
    private IUserAction[] mValidUserActions;

    /**
     * Use this constructor if this Presenter controls one view only.
     */
    public PresenterImpl(Model model, MvpView view, IUserAction[] validUserActions,
                         IMvpQuery[] initialQueries) {
        this(model, new MvpView[]{view}, validUserActions, initialQueries);
    }


    /**
     * Use this constructor if this Presenter controls more than one view.
     */
    public PresenterImpl(Model model, @Nullable MvpView[] views, IUserAction[] validUserActions,
                         IMvpQuery[] initialQueries) {
        mModel = model;
        if (views != null) {
            mUpdatableViews = views;
            for (int i = 0; i < mUpdatableViews.length; i++) {
                mUpdatableViews[i].addListener(this);
            }
        } else {
            LogUtil.e(TAG, "Creating a PresenterImpl with null View");
        }
        mValidUserActions = validUserActions;
        mInitialQueriesToLoad = initialQueries;
    }

    public void cleanUp()
    {
        if(mModel!=null) {
            mModel.cleanUp();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadInitial() {
        // Load data queries if any.
        if (mInitialQueriesToLoad != null && mInitialQueriesToLoad.length > 0) {
            for (int i = 0; i < mInitialQueriesToLoad.length; i++) {
                mModel.requestData(mInitialQueriesToLoad[i], new Model.DataQueryCallback() {
                    @Override
                    public void onModelUpdated(Model model, IMvpQuery query) {

                        if (mUpdatableViews != null) {
                            for (int i = 0; i < mUpdatableViews.length; i++) {
                                mUpdatableViews[i].displayData(model, query);
                            }
                        } else {
                            LogUtil.e(TAG, "loadInitial(), cannot notify a null view!");
                        }
                    }

                    @Override
                    public void onError(IMvpQuery query, Throwable throwable) {
                        if (mUpdatableViews != null) {
                            for (int i = 0; i < mUpdatableViews.length; i++) {
                                mUpdatableViews[i].displayErrorMessage(query,throwable);
                            }
                        } else {
                            LogUtil.e(TAG, "loadInitial(), cannot notify a null view!");
                        }
                    }
                });
            }
        } else {
            // No data query to load, update the view.
            if (mUpdatableViews != null) {
                for (int i = 0; i < mUpdatableViews.length; i++) {
                    mUpdatableViews[i].displayData(mModel, null);
                }
            } else {
                LogUtil.e(TAG, "loadInitialQueries(), cannot notify a null view!");
            }
        }
    }

    /**
     * Called when the user has performed an {@code action}, with data to be passed as a {@link
     * Bundle} in {@code args}.
     * <p/>
     * Add the constants used to store values in the bundle to the Model implementation class as
     * final static protected strings.
     * <p/>
     *
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onUserAction(IUserAction action, @Nullable Bundle args) {
        boolean isValid = false;
        if (mValidUserActions != null && action != null) {
            for (int i = 0; i < mValidUserActions.length; i++) {
                if (mValidUserActions[i].getId() == action.getId()) {
                    isValid = true;
                }
            }
        }
        if(isValid)
        {
            mModel.deliverUserAction(action, args, new Model.UserActionCallback() {
                @Override
                public void onModelUpdated(Model model, IUserAction userAction) {
                    if (mUpdatableViews != null) {
                        for (int i = 0; i < mUpdatableViews.length; i++) {
                            mUpdatableViews[i].displayUserActionResult(model, userAction);
                        }
                    } else {
                        LogUtil.e(TAG, "onUserAction(), cannot notify a null view!");
                    }
                }

                @Override
                public void onError(IUserAction userAction, Throwable throwable) {
                    if (mUpdatableViews != null) {
                        for (int i = 0; i < mUpdatableViews.length; i++) {
                            mUpdatableViews[i].displayUserActionError(null, userAction, throwable);
                        }
                        // User action not understood by model, even though the presenter understands

                        // it.
                        LogUtil.e(TAG, "Model doesn't implement user action " + userAction.getId() +
                                ". Have you forgotten to implement this UserActionEnum in your " +
                                "model," +
                                " or have you called setValidUserActions on your presenter with a " +
                                "UserActionEnum that it shouldn't support?");
                    } else {
                        LogUtil.e(TAG, "onUserAction(), cannot notify a null view!");
                    }
                }
            });
        }
        else
        {
            if (mUpdatableViews != null) {
                // User action not understood.
                for (int i = 0; i < mUpdatableViews.length; i++) {
                    mUpdatableViews[i].displayUserActionError(null, action, new RuntimeException(
                            "Invalid user action " + (action != null ? action.getId() : null) +
                                    ". Have you called setValidUserActions on your presenter, with all " +

                                    "the UserActionEnum you want to support?"));
                }
            } else {
                LogUtil.e(TAG, "onUserAction(), cannot notify a null view!");
            }
        }
    }
}
