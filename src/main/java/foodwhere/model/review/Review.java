package foodwhere.model.review;

import static foodwhere.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import foodwhere.model.detail.Detail;

/**
 * Represents a Review in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Review {

    // Identity fields
    private final StallName name;

    // Data fields
    private final Date date;
    private final Content content;
    private final Set<Detail> details = new HashSet<>();

    /**
     * Every field must be present and not null.
     */
    public Review(StallName name, Date date, Content content, Set<Detail> details) {
        requireAllNonNull(name, date, content, details);
        this.name = name;
        this.date = date;
        this.content = content;
        this.details.addAll(details);
    }

    public StallName getName() {
        return name;
    }

    public Date getDate() {
        return date;
    }

    public Content getContent() {
        return content;
    }

    /**
     * Returns an immutable detail set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Detail> getDetails() {
        return Collections.unmodifiableSet(details);
    }

    /**
     * Returns true if both review have the same stall name.
     * This defines a weaker notion of equality between two reviews.
     */
    public boolean isSameReview(Review otherReview) {
        if (otherReview == this) {
            return true;
        }

        return otherReview != null
                && otherReview.getName().equals(getName());
    }

    /**
     * Returns true if both stalls have the same identity and data fields.
     * This defines a stronger notion of equality between two stalls.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Review)) {
            return false;
        }

        Review otherStall = (Review) other;
        return otherStall.getName().equals(getName())
                && otherStall.getDate().equals(getDate())
                && otherStall.getContent().equals(getContent())
                && otherStall.getDetails().equals(getDetails());
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, date, content, details);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append("; Date: ")
                .append(getDate())
                .append("; Content: ")
                .append(getContent());

        Set<Detail> details = getDetails();
        if (!details.isEmpty()) {
            builder.append("; Details: ");
            details.forEach(builder::append);
        }
        return builder.toString();
    }

}
