package il.ac.hit.view;

import il.ac.hit.viewModel.IViewModel;

/**
 * This interface represent the user interface of the application.
 * @author Inbal mishal and Tal levi
 */
public interface IView {
    /**
     * Create the UI thread and the start frame.
     */
    public void start();

    /**
     * Set the viewModel parameter.
     * @param vm Represent the view model that connected to the model.
     */
    public void setViewModel(IViewModel vm);

    /**
     * Show to the user a message if the action succeeded.
     * @param text Represent the exception message.
     */
    public void showGoodMessage(String text);

    /**
     * Show to the user a message if the action failed.
     * @param text Represent the exception message.
     */
    public void showBadMessage(String text);
}
