package foodwhere.logic.parser;

import static foodwhere.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Set;
import java.util.stream.Stream;

import foodwhere.logic.commands.SAddCommand;
import foodwhere.logic.parser.exceptions.ParseException;
import foodwhere.model.detail.Detail;
import foodwhere.model.stall.Address;
import foodwhere.model.stall.Name;
import foodwhere.model.stall.Stall;

/**
 * Parses input arguments and creates a new SAddCommand object
 */
public class SAddCommandParser implements Parser<SAddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the SAddCommand
     * and returns an SAddCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public SAddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args,
                        CliSyntax.PREFIX_NAME,
                        CliSyntax.PREFIX_ADDRESS,
                        CliSyntax.PREFIX_DETAIL);

        if (!arePrefixesPresent(argMultimap,
                CliSyntax.PREFIX_NAME,
                CliSyntax.PREFIX_ADDRESS)
                || !argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, SAddCommand.MESSAGE_USAGE));
        }

        Name name = ParserUtil.parseName(argMultimap.getValue(CliSyntax.PREFIX_NAME).get());
        Address address = ParserUtil.parseAddress(argMultimap.getValue(CliSyntax.PREFIX_ADDRESS).get());
        Set<Detail> detailList = ParserUtil.parseDetails(argMultimap.getAllValues(CliSyntax.PREFIX_DETAIL));

        Stall stall = new Stall(name, address, detailList);

        return new SAddCommand(stall);
    }

    /**
     * Returns true if none of the prefixes contains empty {@code Optional} values in the given
     * {@code ArgumentMultimap}.
     */
    private static boolean arePrefixesPresent(ArgumentMultimap argumentMultimap, Prefix... prefixes) {
        return Stream.of(prefixes).allMatch(prefix -> argumentMultimap.getValue(prefix).isPresent());
    }

}
