package foodwhere.logic.commands;

import static java.util.Objects.requireNonNull;

import foodwhere.logic.commands.exceptions.CommandException;
import foodwhere.model.Model;
import foodwhere.model.review.comparator.ReviewsComparatorList;

/**
 * Sort and list all reviews in FoodWhere to the user.
 */
public class RSortCommand extends Command {

    public static final String COMMAND_WORD = "rsort";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Sort the review list by specified criteria. See user guide for the list of criteria supported.\n"
            + "Parameters: CRITERIA (case-insensitive)\n"
            + "Example: " + COMMAND_WORD + " name";

    public static final String MESSAGE_SUCCESS = "The review list is now sorted by %1$s";

    private final ReviewsComparatorList reviewsComparator;

    public RSortCommand(ReviewsComparatorList reviewsComparator) {
        this.reviewsComparator = reviewsComparator;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        model.sortReviews(reviewsComparator.getComparator());
        return new CommandResult(String.format(MESSAGE_SUCCESS, reviewsComparator.getCriteria()));
    }
}
