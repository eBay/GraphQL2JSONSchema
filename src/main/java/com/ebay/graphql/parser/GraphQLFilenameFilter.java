package com.ebay.graphql.parser;

import java.io.File;
import java.io.FilenameFilter;

public class GraphQLFilenameFilter implements FilenameFilter {

	@Override
	public boolean accept(File arg0, String arg1) {

		if (new File(arg0, arg1).isDirectory()) {
			return true;
		} else if (arg1.endsWith(".graphql") || arg1.endsWith(".graphqls")) {
			return true;
		}
		return false;
	}

}
