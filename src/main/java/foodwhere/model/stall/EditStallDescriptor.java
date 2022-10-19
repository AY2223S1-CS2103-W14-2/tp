package foodwhere.model.stall;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import foodwhere.commons.util.CollectionUtil;
import foodwhere.model.commons.Name;
import foodwhere.model.commons.Tag;
import foodwhere.model.review.Review;

/**
 * Stores the details to edit the stall with. Each non-empty field value will replace the
 * corresponding field value of the stall.
 */
public class EditStallDescriptor {
    private Name name;
    private Address address;
    private Set<Tag> tags;
    private Set<Review> reviews;

    public EditStallDescriptor() {
        // empty constructor
    }

    /**
     * Copy constructor.
     * A defensive copy of {@code tags} is used internally.
     */
    public EditStallDescriptor(EditStallDescriptor toCopy) {
        setName(toCopy.name);
        setAddress(toCopy.address);
        setTags(toCopy.tags);
        setReviews(toCopy.reviews);
    }

    /**
     * Creates and returns a {@code Stall} with the details of {@code stallToEdit}
     * edited with {@code editStallDescriptor}.
     */
    public static Stall createEditedStall(Stall stallToEdit, EditStallDescriptor editStallDescriptor) {
        assert stallToEdit != null;

        Name updatedName = editStallDescriptor.getName().orElse(stallToEdit.getName());
        Address updatedAddress = editStallDescriptor.getAddress().orElse(stallToEdit.getAddress());
        Set<Tag> updatedTags = editStallDescriptor.getTags().orElse(stallToEdit.getTags());
        Set<Review> updatedReviews = editStallDescriptor.getReviews().orElse(stallToEdit.getReviews());

        return new Stall(updatedName, updatedAddress, updatedTags, updatedReviews);
    }

    /**
     * Returns true if at least one field is edited.
     */
    public boolean isAnyFieldEdited() {
        return CollectionUtil.isAnyNonNull(name, address, tags, reviews);
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

    /**
     * Sets {@code reviews} to this object's {@code reviews}.
     * A defensive copy of {@code reviews} is used internally.
     */
    public void setReviews(Set<Review> reviews) {
        this.reviews = (reviews != null) ? new HashSet<>(reviews) : null;
    }

    /**
     * Returns an unmodifiable review set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     * Returns {@code Optional#empty()} if {@code tags} is null.
     */
    public Optional<Set<Review>> getReviews() {
        return (reviews != null) ? Optional.of(Collections.unmodifiableSet(reviews)) : Optional.empty();
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
