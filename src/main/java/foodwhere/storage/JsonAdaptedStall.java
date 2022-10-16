package foodwhere.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import foodwhere.commons.exceptions.IllegalValueException;
import foodwhere.model.commons.Name;
import foodwhere.model.commons.Tag;
import foodwhere.model.review.Review;
import foodwhere.model.stall.Address;
import foodwhere.model.stall.Stall;

/**
 * Jackson-friendly version of {@link Stall}.
 */
class JsonAdaptedStall {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Stall's %s field is missing!";

    private final String name;
    private final String address;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final List<JsonAdaptedReview> reviews = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedStall} with the given stall details.
     */
    @JsonCreator
    public JsonAdaptedStall(@JsonProperty("name") String name,
                            @JsonProperty("address") String address,
                            @JsonProperty("tags") List<JsonAdaptedTag> tags,
                            @JsonProperty("reviews") List<JsonAdaptedReview> reviews) {
        this.name = name;
        this.address = address;
        if (tags != null) {
            this.tags.addAll(tags);
        }
        if (reviews != null) {
            this.reviews.addAll(reviews);
        }
    }

    /**
     * Converts a given {@code Stall} into this class for Jackson use.
     */
    public JsonAdaptedStall(Stall source) {
        name = source.getName().fullName;
        address = source.getAddress().value;
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Checks if a review is of this stall.
     *
     * @throws IllegalValueException if there are data constraints violated in the name of the stall.
     */
    public boolean isReviewOfStall(Review review) throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        return name.equals(review.getName().fullName);
    }

    /**
     * Adds the review to the stall.
     */
    public void addReview(Review review) {
        reviews.add(new JsonAdaptedReview(review));
    }

    /**
     * Converts this Jackson-friendly adapted stall object into the model's {@code Stall} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted stall.
     */
    public Stall toModelType() throws IllegalValueException {
        final List<Tag> stallTags = new ArrayList<>();
        for (JsonAdaptedTag tag : this.tags) {
            stallTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        final Set<Tag> modelTags = new HashSet<>(stallTags);

        final Set<Review> modelReviews = getModelReviews();
        return new Stall(modelName, modelAddress, modelTags, modelReviews);
    }

    /**
     * Converts the reviews in this Jackson-friendly adapted stall object into the model's {@code Review} objects.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted stall.
     */
    public Set<Review> getModelReviews() throws IllegalValueException {
        final Set<Review> modelReviews = new HashSet<>();

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelStallName = new Name(name);

        for (JsonAdaptedReview review : reviews) {
            modelReviews.add(review.toModelType(modelStallName));
        }
        return modelReviews;
    }
}
