package com.vivek.tsystem.mvparch;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by vivek on 04/08/17.
 */

public interface Model<Q extends IMvpQuery, UA extends IUserAction> {

    /**
     * @return an array of {@link IMvpQuery} that can be processed by the model
     */
    Q[] getAllQueries();

    /**
     * @return an array of {@link IUserAction} that can be processed by model
     */
    UA[] getUserActions();

    /**
     * Delivers a user {@code action} and associated {@code args} to the Model, which typically will
     * run a data update. The Model then notify the {@link Presenter} it is done with the user
     * action via the {@code callback}.
     * <p/>
     * Add the constants used to store values in the bundle to the Model implementation class as
     * final static protected strings.
     */
    void deliverUserAction(UA action, @Nullable Bundle args, UserActionCallback userActionCallback);

    /**
     * Requests the Model to load data for the given {@code query}, then notify the data query was
     * completed via the {@code callback}. Typically, this is called to initialise the model with
     * the data needed to display the UI when loading.
     */
    void requestData(Q query, DataQueryCallback callback);

    void cleanUp();

    /**
     * A callback used to notify the {@link Presenter} that the update for a given {@link IMvpQuery}
     * has completed, either successfully or with error.
     */
    interface DataQueryCallback<M extends Model, Q extends IMvpQuery> {

        public void onModelUpdated(M model, Q query);

        public void onError(Q query, Throwable e);
    }

    /**
     * A callback used to notify the {@link Presenter} that the update for a given {@link
     * IUserAction} has completed, either successfully or with error.
     */
    interface UserActionCallback<M extends Model, UA extends IUserAction> {

        public void onModelUpdated(M model, UA userAction);

        public void onError(UA userAction, Throwable e);

    }



}
