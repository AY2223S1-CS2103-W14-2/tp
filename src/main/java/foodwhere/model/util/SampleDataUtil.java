package foodwhere.model.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import foodwhere.model.AddressBook;
import foodwhere.model.ReadOnlyAddressBook;
import foodwhere.model.commons.Detail;
import foodwhere.model.commons.Name;
import foodwhere.model.review.Content;
import foodwhere.model.review.Date;
import foodwhere.model.review.Review;
import foodwhere.model.stall.Address;
import foodwhere.model.stall.Stall;

/**
 * Contains utility methods for populating {@code AddressBook} with sample data.
 */
public class SampleDataUtil {
    public static Stall[] getSampleStalls() {
        return new Stall[] {
            new Stall(new Name("Alex Chicken Rice"), new Address("Blk 30 Geylang Street 29, #06-40"),
                    getDetailSet("chickenrice")),
            new Stall(new Name("Char Char Kuey Tiao"), new Address("Blk 30 Lorong 3 Serangoon Gardens, #07-18"),
                    getDetailSet("charkwaytiao")),
            new Stall(new Name("Yu Bak Chor Mee"), new Address("Blk 11 Ang Mo Kio Street 74, #11-04"),
                    getDetailSet("bakchormee")),
            new Stall(new Name("Irfan Muslim Food"), new Address("Blk 436 Serangoon Gardens Street 26, #16-43"),
                    getDetailSet("family", "halal", "muslim"))
        };
    }

    public static Review[] getSampleReviews() {
        return new Review[] {
            new Review(new Name("Alex Chicken Rice"), new Date("2022-09-20"),
                    new Content("Very tasty. Worth the trip"), getDetailSet("travelworthy")),
            new Review(new Name("Irfan Muslim Food"), new Date("2022-09-20"),
                    new Content("Very affordable"), getDetailSet("halal"))
        };
    }

    public static ReadOnlyAddressBook getSampleAddressBook() {
        AddressBook sampleAb = new AddressBook();
        for (Stall sampleStall : getSampleStalls()) {
            sampleAb.addStall(sampleStall);
        }

        for (Review sampleReview : getSampleReviews()) {
            sampleAb.addReview(sampleReview);
        }
        return sampleAb;
    }

    /**
     * Returns a detail set containing the list of strings given.
     */
    public static Set<Detail> getDetailSet(String... strings) {
        return Arrays.stream(strings)
                .map(Detail::new)
                .collect(Collectors.toSet());
    }

    /**
     * Returns a review set containing the list of reviews given.
     */
    public static Set<Review> getReviewSet(Review... reviews) {
        return new HashSet<>(List.of(reviews));
    }

}
