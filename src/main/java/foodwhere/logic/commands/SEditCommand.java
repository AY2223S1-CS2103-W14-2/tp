package foodwhere.logic.commands;

import static foodwhere.model.Model.PREDICATE_SHOW_ALL_REVIEWS;
import static foodwhere.model.Model.PREDICATE_SHOW_ALL_STALLS;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import foodwhere.commons.core.Messages;
import foodwhere.commons.core.index.Index;
import foodwhere.commons.util.CollectionUtil;
import foodwhere.logic.commands.exceptions.CommandException;
import foodwhere.logic.parser.CliSyntax;
import foodwhere.model.Model;
import foodwhere.model.commons.Name;
import foodwhere.model.commons.Tag;
import foodwhere.model.review.Review;
import foodwhere.model.stall.Address;
import foodwhere.model.stall.Stall;

/**
 * Edits the details of an existing stall in the address book.
 */
public class SEditCommand extends Command {

    public static final String COMMAND_WORD = "sedit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edits the details of the stall identified "
            + "by the index number used in the displayed stall list. "
            + "Existing values will be overwritten by the input values.\n"
            + "Parameters: INDEX (must be a positive integer) "
            + "[" + CliSyntax.PREFIX_NAME + "NAME] "
            + "[" + CliSyntax.PREFIX_ADDRESS + "ADDRESS] "
            + "[" + CliSyntax.PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 ";

    public static final String MESSAGE_EDIT_STALL_SUCCESS = "Edited Stall: %1$s";
    public static final String MESSAGE_NOT_EDITED = "At least one field to edit must be provided.";
    public static final String MESSAGE_DUPLICATE_STALL = "This stall already exists in the address book.";

    private final Index index;
    private final EditStallDescriptor editStallDescriptor;

    /**
     * @param index of the stall in the filtered stall list to edit
     * @param editStallDescriptor details to edit the stall with
     */
    public SEditCommand(Index index, EditStallDescriptor editStallDescriptor) {
        requireNonNull(index);
        requireNonNull(editStallDescriptor);

        this.index = index;
        this.editStallDescriptor = new EditStallDescriptor(editStallDescriptor);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Stall> lastShownList = model.getFilteredStallList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_STALL_DISPLAYED_INDEX);
        }

        Stall stallToEdit = lastShownList.get(index.getZeroBased());
        Stall editedStall = createEditedStall(stallToEdit, editStallDescriptor);

        if (!stallToEdit.isSameStall(editedStall) && model.hasStall(editedStall)) {
            throw new CommandException(MESSAGE_DUPLICATE_STALL);
        }

        if (!stallToEdit.getReviews().isEmpty() && !stallToEdit.getName().equals(editedStall.getName())) {
            Review[] reviewsToEdit = stallToEdit.getReviews().toArray(new Review[stallToEdit.getReviews().size()]);
            Review[] editedReviews = editedStall.getReviews().toArray(new Review[editedStall.getReviews().size()]);

            assert (reviewsToEdit.length == editedReviews.length);

            for (int i = 0; i < editedReviews.length; i++) {
                model.setReview(reviewsToEdit[i], editedReviews[i]);
            }
            model.updateFilteredReviewList(PREDICATE_SHOW_ALL_REVIEWS);
        }


        model.setStall(stallToEdit, editedStall);
        model.updateFilteredStallList(PREDICATE_SHOW_ALL_STALLS);

        return new CommandResult(String.format(MESSAGE_EDIT_STALL_SUCCESS, editedStall));
    }

    /**
     * Creates and returns a {@code Stall} with the details of {@code stallToEdit}
     * edited with {@code editStallDescriptor}.
     */
    private static Stall createEditedStall(Stall stallToEdit, EditStallDescriptor editStallDescriptor) {
        assert stallToEdit != null;

        Name updatedName = editStallDescriptor.getName().orElse(stallToEdit.getName());
        Address updatedAddress = editStallDescriptor.getAddress().orElse(stallToEdit.getAddress());
        Set<Tag> updatedTags = editStallDescriptor.getTags().orElse(stallToEdit.getTags());

        Set<Review> reviewsToUpdate = stallToEdit.getReviews();
        Set<Review> updatedReviews = new HashSet<>();
        if (updatedName.equals(stallToEdit.getName())) {
            updatedReviews = reviewsToUpdate;
        } else {
            if (!reviewsToUpdate.isEmpty()) {
                for (Review review : reviewsToUpdate) {
                    Review updatedReview = new Review(
                            updatedName,
                            review.getDate(),
                            review.getContent(),
                            review.getRating(),
                            review.getTags());
                    updatedReviews.add(updatedReview);
                }
            }
        }

        return new Stall(updatedName, updatedAddress, updatedTags, updatedReviews);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SEditCommand)) {
            return false;
        }

        // state check
        SEditCommand e = (SEditCommand) other;
        return index.equals(e.index)
                && editStallDescriptor.equals(e.editStallDescriptor);
    }

    /**
     * Stores the details to edit the stall with. Each non-empty field value will replace the
     * corresponding field value of the stall.
     */
    public static class EditStallDescriptor {
        private Name name;
        private Address address;
        private Set<Tag> tags;

        public EditStallDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code tags} is used internally.
         */
        public EditStallDescriptor(EditStallDescriptor toCopy) {
            setName(toCopy.name);
            setAddress(toCopy.address);
            setTags(toCopy.tags);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(name, address, tags);
        }

        public void setName(Name name) {
            this.name = name;
        }

        public Optional<Name> getName() {
            return Optional.ofNullable(name);
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public Optional<Address> getAddress() {
            return Optional.ofNullable(address);
        }

        /**
         * Sets {@code tags} to this object's {@code tags}.
         * A defensive copy of {@code tags} is used internally.
         */
        public void setTags(Set<Tag> tags) {
            this.tags = (tags != null) ? new HashSet<>(tags) : null;
        }

        /**
         * Returns an unmodifiable tag set, which throws {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code tags} is null.
         */
        public Optional<Set<Tag>> getTags() {
            return (tags != null) ? Optional.of(Collections.unmodifiableSet(tags)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof EditStallDescriptor)) {
                return false;
            }

            // state check
            EditStallDescriptor e = (EditStallDescriptor) other;

            return getName().equals(e.getName())
                    && getAddress().equals(e.getAddress())
                    && getTags().equals(e.getTags());
        }
    }
}
