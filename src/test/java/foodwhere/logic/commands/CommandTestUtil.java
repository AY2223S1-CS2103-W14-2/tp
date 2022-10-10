package foodwhere.logic.commands;

import static foodwhere.testutil.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import foodwhere.commons.core.index.Index;
import foodwhere.logic.commands.exceptions.CommandException;
import foodwhere.logic.parser.CliSyntax;
import foodwhere.model.AddressBook;
import foodwhere.model.Model;
import foodwhere.model.stall.NameContainsKeywordsPredicate;
import foodwhere.model.stall.Stall;
import foodwhere.testutil.EditStallDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_ADDRESS_AMY = "Block 312, Amy Street 1";
    public static final String VALID_ADDRESS_BOB = "Block 123, Bobby Street 3";
    public static final String VALID_DATE_AMY = "1/1/2020";
    public static final String VALID_DATE_BOB = "31/12/2021";
    public static final String VALID_CONTENT_AMY = "Good, 5/5";
    public static final String VALID_CONTENT_BOB = "Bad, 1/5";
    public static final String VALID_DETAIL_HUSBAND = "husband";
    public static final String VALID_DETAIL_FRIEND = "friend";

    public static final String NAME_DESC_AMY = " " + CliSyntax.PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + CliSyntax.PREFIX_NAME + VALID_NAME_BOB;
    public static final String ADDRESS_DESC_AMY = " " + CliSyntax.PREFIX_ADDRESS + VALID_ADDRESS_AMY;
    public static final String ADDRESS_DESC_BOB = " " + CliSyntax.PREFIX_ADDRESS + VALID_ADDRESS_BOB;
    public static final String DETAIL_DESC_FRIEND = " " + CliSyntax.PREFIX_DETAIL + VALID_DETAIL_FRIEND;
    public static final String DETAIL_DESC_HUSBAND = " " + CliSyntax.PREFIX_DETAIL + VALID_DETAIL_HUSBAND;

    public static final String INVALID_NAME_DESC = " " + CliSyntax.PREFIX_NAME + "James&"; // '&' not allowed in names
    public static final String INVALID_ADDRESS_DESC = " "
            + CliSyntax.PREFIX_ADDRESS; // empty string not allowed for addresses
    public static final String INVALID_DETAIL_DESC = " "
            + CliSyntax.PREFIX_DETAIL + "hubby*"; // '*' not allowed in details

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditStallDescriptor DESC_AMY;
    public static final EditCommand.EditStallDescriptor DESC_BOB;

    static {
        DESC_AMY = new EditStallDescriptorBuilder().withName(VALID_NAME_AMY)
                .withAddress(VALID_ADDRESS_AMY)
                .withDetails(VALID_DETAIL_FRIEND).build();
        DESC_BOB = new EditStallDescriptorBuilder().withName(VALID_NAME_BOB)
                .withAddress(VALID_ADDRESS_BOB)
                .withDetails(VALID_DETAIL_HUSBAND, VALID_DETAIL_FRIEND).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered stall list and selected stall in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        List<Stall> expectedFilteredList = new ArrayList<>(actualModel.getFilteredStallList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedAddressBook, actualModel.getAddressBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredStallList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the stall at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showStallAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredStallList().size());

        Stall stall = model.getFilteredStallList().get(targetIndex.getZeroBased());
        final String[] splitName = stall.getName().fullName.split("\\s+");
        model.updateFilteredStallList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredStallList().size());
    }
}
