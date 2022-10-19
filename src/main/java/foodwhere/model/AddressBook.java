package foodwhere.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import foodwhere.model.review.Review;
import foodwhere.model.review.UniqueReviewList;
import foodwhere.model.stall.EditStallDescriptor;
import foodwhere.model.stall.Stall;
import foodwhere.model.stall.UniqueStallList;
import javafx.collections.ObservableList;

/**
 * Wraps all data at the address-book level
 * Duplicates are not allowed (by .isSameStall comparison)
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniqueStallList stalls;
    private final UniqueReviewList reviews;

    /*
     * The 'unusual' code block below is a non-static initialization block, sometimes used to avoid duplication
     * between constructors. See https://docs.oracle.com/javase/tutorial/java/javaOO/initial.html
     *
     * Note that non-static init blocks are not recommended to use. There are other ways to avoid duplication
     *   among constructors.
     */
    {
        stalls = new UniqueStallList();
        reviews = new UniqueReviewList();
    }

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Stalls in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
        refreshReviews();
    }

    /**
     * Sets {@code reviews} correctly.
     */
    private void refreshReviews() {
        ArrayList<Review> current = new ArrayList<>();
        for (Stall stall : stalls) {
            for (Review review : stall.getReviews()) {
                current.add(review);
            }
        }
        ArrayList<Review> toRemove = new ArrayList<>();
        for (Review review : reviews) {
            if (!current.contains(review)) {
                toRemove.remove(review);
            }
        }
        for (Review review : toRemove) {
            reviews.remove(review);
        }
        for (Review review : current) {
            if (!reviews.contains(review)) {
                reviews.add(review);
            }
        }
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the stall list with {@code stalls}.
     * {@code stalls} must not contain duplicate stalls.
     */
    public void setStalls(List<Stall> stalls) {
        this.stalls.setStalls(stalls);
        refreshReviews();
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);
        setStalls(newData.getStallList());
        refreshReviews();
    }

    //// stall-level operations

    /**
     * Returns true if a stall with the same identity as {@code stall} exists in the address book.
     */
    public boolean hasStall(Stall stall) {
        requireNonNull(stall);
        return stalls.contains(stall);
    }

    /**
     * Adds a stall to the address book.
     * The stall must not already exist in the address book.
     */
    public void addStall(Stall p) {
        stalls.add(p);
        refreshReviews();
    }

    /**
     * Replaces the given stall {@code target} in the list with {@code editedStall}.
     * {@code target} must exist in the address book.
     * The stall identity of {@code editedStall} must not be the same as another existing stall in the address book.
     */
    public void setStall(Stall target, Stall editedStall) {
        requireNonNull(editedStall);
        stalls.setStall(target, editedStall);
        refreshReviews();
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeStall(Stall key) {
        stalls.remove(key);
        refreshReviews();
    }

    /**
     * Sorts the stall list by name.
     */
    public void sortStalls() {
        stalls.sort();
    }

    //// review methods

    /**
     * Returns true if a review with the same identity as {@code review} exists in the address book.
     */
    public boolean hasReview(Review review) {
        requireNonNull(review);
        refreshReviews();
        return reviews.contains(review);
    }

    /**
     * Adds a review to the address book.
     * The review must not already exist in the address book.
     */
    public void addReviewToStall(Review p, Stall s) {
        Set<Review> originalReviews = s.getReviews();
        if (originalReviews.contains(p)) {
            return;
        }
        EditStallDescriptor editStallDescriptor = new EditStallDescriptor();
        HashSet<Review> editedReviews = new HashSet<>(originalReviews);
        editedReviews.add(p);
        editStallDescriptor.setReviews(editedReviews);
        Stall newStall = EditStallDescriptor.createEditedStall(s, editStallDescriptor);
        stalls.setStall(s, newStall);
        refreshReviews();
    }

    /**
     * Replaces the given review {@code target} in the list with {@code editedReview}.
     * {@code target} must exist in the address book.
     * The review identity of {@code editedReview} must not be the same as another existing review in the address book.
     */
    public void setReview(Review target, Review editedReview) {
        requireNonNull(editedReview);
        refreshReviews();
        reviews.setReview(target, editedReview);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removeReview(Review key) {
        refreshReviews();
        // remove from stall
        boolean deleted = false;
        for (Stall stall : stalls) {
            Set<Review> stallReviews = stall.getReviews();
            if (stallReviews.contains(key)) {
                HashSet<Review> newReviews = new HashSet<>(stallReviews);
                newReviews.remove(key);
                EditStallDescriptor editedStall = new EditStallDescriptor();
                editedStall.setReviews(newReviews);
                Stall newStall = EditStallDescriptor.createEditedStall(stall, editedStall);
                setStall(stall, newStall);
                deleted = true;
                break;
            }
        }
        assert(deleted);
        refreshReviews();
    }

    /**
     * Sorts the review list by name.
     */
    public void sortReviews() {
        reviews.sort();
    }

    //// util methods

    @Override
    public String toString() {
        refreshReviews();
        return stalls.asUnmodifiableObservableList().size() + " stalls, "
                + reviews.asUnmodifiableObservableList().size() + " reviews";
        // TODO: refine later
    }

    @Override
    public ObservableList<Stall> getStallList() {
        return stalls.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<Review> getReviewList() {
        refreshReviews();
        return reviews.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddressBook // instanceof handles nulls
                && stalls.equals(((AddressBook) other).stalls));
    }

    @Override
    public int hashCode() {
        refreshReviews();
        return Objects.hash(stalls, reviews);
    }
}
