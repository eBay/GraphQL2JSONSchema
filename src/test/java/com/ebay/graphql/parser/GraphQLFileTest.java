package com.ebay.graphql.parser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.hamcrest.Matchers;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class GraphQLFileTest {
	
	GraphQLFile file; 
	
	@BeforeMethod
	public void setup() {
		file = new GraphQLFile(Arrays.asList("one", "two", "three"));
	}

	@Test
	public void getFirstLineNumber() {
		assertThat(file.getCurrentLineNumber(), is(equalTo(1)));
	}
	
	@Test
	public void getLineAndCheckLineNumber() {
		file.getNextLine();
		assertThat(file.getCurrentLineNumber(), is(equalTo(2)));
	}
	
	@Test
	public void hasMoreLines() {
		assertThat(file.hasMoreLines(), is(equalTo(true)));
	}
	
	@Test
	public void doesNotHaveMoreLines() {
		file.getNextLine();
		file.getNextLine();
		file.getNextLine();
		assertThat(file.hasMoreLines(), is(equalTo(false)));
	}
	
	@Test
	public void getNumberOfLines() {
		assertThat(file.getNumberOfLines(), is(equalTo(3)));
	}
	
	@Test
	public void getLineAtLineNumber() {
		assertThat(file.getLine(2), is(equalTo("two")));
	}
	
	@Test
	public void peekNextLine() {
		assertThat(file.peekNextLine(), is(equalTo("two")));
		file.getNextLine();
		assertThat(file.peekNextLine(), is(equalTo("three")));
	}
	
	@Test
	public void getCurrentLine() {
		assertThat(file.getCurrentLine(), is(equalTo("one")));
		assertThat(file.getCurrentLine(), is(equalTo("one"))); // Confirm it doesn't iterate to next line.
		file.getNextLine();
		assertThat(file.getCurrentLine(), is(equalTo("two")));
	}
	
	@Test
	public void getCurentLineAndAdvance() {
		assertThat(file.getCurrentLine(), is(equalTo("one")));
		assertThat(file.getCurrentLineAndThenAdvance(), is(equalTo("one")));
		assertThat(file.getCurrentLine(), is(equalTo("two")));
	}
	
	@Test
	public void readPastLastLine() {
		file.getNextLine();
		file.getNextLine();
		file.getNextLine();
		assertThat(file.getNextLine(), is(nullValue()));
	}
	
	@Test
	public void peekNextLineWhenLinesAreNull() {		
		File file = new File("~");
		GraphQLFile graphQLFile = new GraphQLFile(file);
		String nextLine = graphQLFile.peekNextLine();
		assertThat(nextLine, is(nullValue()));
	}
	
	@Test
	public void peekNextLineWhenLinesExceeded() {
		List<String> lines = Arrays.asList("one", "two");
		GraphQLFile graphQLFile = new GraphQLFile(lines);
		String line = graphQLFile.getNextLine();
		assertThat(line, is(equalTo("two")));
		line = graphQLFile.getNextLine();
		assertThat(line, is(nullValue()));
		line = graphQLFile.peekNextLine();
		assertThat(line, is(nullValue())); 
	}
	
	@Test
	public void getCurrentLineWhenLinesAreNull() {
		File file = new File("~");
		GraphQLFile graphQLFile = new GraphQLFile(file);
		String line = graphQLFile.getCurrentLine();
		assertThat(line, is(nullValue()));
	}
	
	@Test
	public void getCurrentLineWhenLinesExceeded() {
		List<String> lines = Arrays.asList("one", "two");
		GraphQLFile graphQLFile = new GraphQLFile(lines);
		String line = graphQLFile.getNextLine();
		assertThat(line, is(equalTo("two")));
		line = graphQLFile.getNextLine();
		assertThat(line, is(nullValue()));
		line = graphQLFile.getCurrentLine();
		assertThat(line, is(nullValue())); 
	}
	
	@Test
	public void getNumberOfLinesWhenLinesAreNull() {
		File file = new File("~");
		GraphQLFile graphQLFile = new GraphQLFile(file);
		int numberOfLines = graphQLFile.getNumberOfLines();
		assertThat(numberOfLines, is(equalTo(0)));
	}
	
	@Test
	public void getLineWhenLinesAreNull() {
		File file = new File("~");
		GraphQLFile graphQLFile = new GraphQLFile(file);
		String line = graphQLFile.getLine(1);
		assertThat(line, is(nullValue()));
	}
	
	@Test
	public void getLineWhenLineNumberExceedsRange() {
		List<String> lines = Arrays.asList("one", "two");
		GraphQLFile graphQLFile = new GraphQLFile(lines);
		String line = graphQLFile.getLine(3);
		assertThat(line, is(nullValue()));
		line = graphQLFile.getLine(0);
		assertThat(line, is(nullValue()));
	}
}
