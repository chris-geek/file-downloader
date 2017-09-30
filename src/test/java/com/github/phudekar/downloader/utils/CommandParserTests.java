package com.github.phudekar.downloader.utils;

import com.github.phudekar.downloader.exceptions.InvalidCommandException;

import samples.utils.Command;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CommandParserTests {

    @Test
    public void shouldParseUrlAndLocation() throws InvalidCommandException {
        CommandParser parser = new CommandParser();
        String url = "http://foo.com/bar.txt";
        String location = " ./abc.txt";
        String[] args = new String[]{url, location};

        Command command = parser.parse(args);

        assertThat(command.getUrl(), is(url));
        assertThat(command.getLocation(), is(location.trim()));
    }

    @Test
    public void shouldSetDefaultLocation() throws InvalidCommandException {
        CommandParser parser = new CommandParser();
        String url = "http://foo.com/bar.txt ";
        String[] args = new String[]{url};

        Command command = parser.parse(args);

        assertThat(command.getUrl(), is(url.trim()));
        assertThat(command.getLocation(), is("."));
    }

    @Test(expected = InvalidCommandException.class)
    public void shouldThrowMissingURLException() throws InvalidCommandException {
        CommandParser parser = new CommandParser();
        String url = "http://foo.com/bar.txt";
        String[] args = new String[]{};

        parser.parse(args);
    }
}
