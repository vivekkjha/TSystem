package com.vivek.tsystem.mvparch;

/**
 * Created by vivek on 04/08/17.
 *
 * A Presenter acts as a controller for both the {@link MvpView} (typically a fragment) and
 * the {@link Model} for the MVP architectural pattern.
 * <p/>
 * It is parametrised by the {@link IMvpQuery} (the list of queries it is able to pass on to the
 * Model) and the {@link IUserAction} (the list of user actions it is able to pass on to the
 * Model).
 * <p/>
 * Typically, upon being created, a Presenter requests the Model to load the initial data, then
 * update the View accordingly.
 * <p/>
 * The implementation of a Presenter listens to events generated by the {@link MvpView} (by
 * implementing {@link com.vivek.tsystem.mvparch.MvpView.UserActionListener}, and passes them on to the {@link Model}, which modifies its data as
 * required. The Presenter also listens to events generated by the {@link Model} (by using {@link
 * com.vivek.tsystem.mvparch.Model.DataQueryCallback} and {@link
 * com.vivek.tsystem.mvparch.Model.UserActionCallback} and then asks the {@link
 * MvpView} to update itself.
 */

public interface Presenter {

    /**
     * Requests the model to load the initial data.
     */
    public void loadInitial();
}