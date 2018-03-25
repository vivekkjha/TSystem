package com.vivek.tsystem.mvparch;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by vivek on 04/08/17.
 *
 * An UpdatableView is a UI class, often a {@link android.app.Fragment}, that provides a {@link
 * Presenter} an interface through which to control it  (MVP architectural pattern). It is
 * parametrised by the {@link Model} class, the {@link IMvpQuery} (the list of queries it needs to
 * run to display its initial state) and the {@link IUserAction} (the list of user actions it
 * provides to the user).
 * <p/>
 * Once the data queries are loaded in the Model, the {@link Presenter} updates the UpdatableView by
 * calling {@link #displayData(Object, IMvpQuery)} or {@link #displayErrorMessage(IMvpQuery,Throwable)} so the
 * UpdatableView can redraw itself with the updated data.
 * <p/>
 * The {@link Presenter} registers itself as a {@link UserActionListener} with {@link
 * #addListener(UserActionListener)}, so that it can trigger an update on the {@link Model} when the
 * user performs an action on the UpdatableView. After the data is updated, the {@link Presenter}
 * updates the UpdatableView by calling {@link #displayUserActionResult(Object, IUserAction)} (Object, IUserAction,
 * boolean)}.
 * <p/>
 * The UpdatableView belongs to an {@link android.app.Activity} that typically has been started with
 * an {@link android.content.Intent} specifying at least one Data URI, used for loading the initial
 * data into the {@link Model}.
 */

public interface MvpView<M,Q extends IMvpQuery,UA extends IUserAction>{
    /**
     * Updates the view based on data in the model.
     *
     * @param model The updated model.
     * @param query The query that has triggered the model update. This is so not the full view has
     *              to be updated but only specific elements of the view, depending on the query.
     */
     void displayData(M model, Q query);

    /**
     * Displays error message resulting from a query not succeeding.
     *
     * @param query The query that resulted in an error.
     */
     void displayErrorMessage(Q query, Throwable t);

    /**
     * Updates the view based on the data in the model, following a user action and a success status
     * in updating the data.
     * <p/>
     * When a user action has been carried out, quite often, the View is already up to date with the
     * action (eg a checked box), so this method is typically used to show messages, such as a Toast
     * to confirm the success of an operation.
     *
     * @param model      The updated model.
     * @param userAction The user action that has triggered the model updated. This is so not the
     *                   full view has to be updated but only specific elements of the view,
     *                   depending on the user action.
     */
     void displayUserActionResult(M model, UA userAction);

    void displayUserActionError(M model, UA userAction, Throwable throwable);

    Context getContext();

     void addListener(UserActionListener listener);

    /**
     * A listener for events fired off by a {@link Model}
     */
    interface UserActionListener<UA extends IUserAction> {

        /**
         * Called when the user has performed an {@code action}, with data to be passed as a {@link
         * Bundle} in {@code args}.
         * <p/>
         * Add the constants used to store values in the bundle to the Model implementation class as
         * final static protected strings.
         */
        public void onUserAction(UA action, @Nullable Bundle args);
    }

}
