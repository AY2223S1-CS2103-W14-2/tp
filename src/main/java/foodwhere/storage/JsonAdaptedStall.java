package foodwhere.storage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import foodwhere.commons.exceptions.IllegalValueException;
import foodwhere.model.detail.Detail;
import foodwhere.model.stall.Address;
import foodwhere.model.stall.Name;
import foodwhere.model.stall.Stall;

/**
 * Jackson-friendly version of {@link Stall}.
 */
class JsonAdaptedStall {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Stall's %s field is missing!";

    private final String name;
    private final String address;
    private final List<JsonAdaptedDetail> details = new ArrayList<>();
    private final List<JsonAdaptedStall> reviews = new ArrayList<>();

    /**
     * Constructs a {@code JsonAdaptedStall} with the given stall details.
     */
    @JsonCreator
    public JsonAdaptedStall(@JsonProperty("name") String name,
                            @JsonProperty("address") String address,
                            @JsonProperty("details") List<JsonAdaptedDetail> details,
                            @JsonProperty("reviews") List<JsonAdaptedStall> reviews) {
        this.name = name;
        this.address = address;
        if (details != null) {
            this.details.addAll(details);
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
        details.addAll(source.getDetails().stream()
                .map(JsonAdaptedDetail::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted stall object into the model's {@code Stall} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted stall.
     */
    public Stall toModelType() throws IllegalValueException {
        final List<Detail> stallDetails = new ArrayList<>();
        for (JsonAdaptedDetail detail : details) {
            stallDetails.add(detail.toModelType());
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

        final Set<Detail> modelDetails = new HashSet<>(stallDetails);

        return new Stall(modelName, modelAddress, modelDetails);
    }
}
