package foodwhere.logic.parser;

import static foodwhere.logic.parser.CommandParserTestUtil.assertParseFailure;
import static foodwhere.logic.parser.CommandParserTestUtil.assertParseSuccess;

import foodwhere.logic.commands.SDeleteCommand;
import org.junit.jupiter.api.Test;

import foodwhere.commons.core.Messages;
import foodwhere.testutil.TypicalIndexes;

/**
 * As we are only doing white-box testing, our test cases do not cover path variations
 * outside of the SDeleteCommand code. For example, inputs "1" and "1 abc" take the
 * same path through the SDeleteCommand, and therefore we test only one of them.
 * The path variation for those two cases occur inside the ParserUtil, and
 * therefore should be covered by the ParserUtilTest.
 */
public class SDeleteCommandParserTest {

    private SDeleteCommandParser parser = new SDeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new SDeleteCommand(TypicalIndexes.INDEX_FIRST_STALL));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, SDeleteCommand.MESSAGE_USAGE));
    }
}
