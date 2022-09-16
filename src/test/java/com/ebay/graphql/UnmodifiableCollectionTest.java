package com.ebay.graphql;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class UnmodifiableCollectionTest {

	@Test
	public void confirmAllGettersThatReturnCollectionsAreUnmodifiable() throws ClassNotFoundException {
		
		List<File> files = getAllFilesToCheck();
		Class<?> clazz;
		Method[] methods;
		
		for (File file : files) {
			clazz = Class.forName(getFullyQualifiedClassName(file));
			methods = clazz.getMethods();
			for (Method method : methods) {
				Class<?> returnType = method.getReturnType();
				if (returnType.isAssignableFrom(List.class)) {
					assertUnmodifiable(method, clazz, true);
				} else if (returnType.isAssignableFrom(Map.class)) {
					assertUnmodifiable(method, clazz, false);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<File> getAllFilesToCheck() {
		String currentWorkingDirectory = System.getProperty("user.dir");
		File rootDirectory = new File(currentWorkingDirectory, "src/main/java");
		return new ArrayList<>(FileUtils.listFiles(rootDirectory, new String[] {"java"}, true));
	}
	
	private String getFullyQualifiedClassName(File file) {
		
		String fullyQualifiedPath = "";
		String step = file.getName().substring(0, file.getName().indexOf("."));
		File parent = file.getParentFile();
		while (!"com".equals(step) && parent != null) {
			
			if (!fullyQualifiedPath.isEmpty()) {
				fullyQualifiedPath = "." + fullyQualifiedPath;
			}
			
			fullyQualifiedPath = step + fullyQualifiedPath;
			step = parent.getName();
			parent = parent.getParentFile();
		}
		
		if ("com".equals(step)) {
			fullyQualifiedPath = step + "." + fullyQualifiedPath;
		}
		
		return fullyQualifiedPath;
	}
	
	// Shamelessly stolen everyting below from : 
	// https://github.corp.ebay.com/bshanka/picaxo-master/blob/master/XOApplication/XOApplicationTests/src/com/ebay/xo/app/test/util/UnmodifiableCollectionTestUtils.java
	// Slight changes made (Assert to MatcherAssert & not requiring setter call - everything expected to initialize to empty collection).
	private void assertUnmodifiable(Method getMethod, Class<?> testClass, boolean list) {
		String methodName = getMethod.getName();
		String baseName = methodName.substring(3, methodName.length());
		try {
			
			// first create a new instance
			Object testInstance = testClass.newInstance();

			if (list) {
				checkListModificationBlocked(getMethod, testInstance);
			} else {
				checkMapModificationBlocked(getMethod, testInstance);
			}
		} catch (RuntimeException e) {
			Assert.fail(e.getMessage() + " problems asserting unmodifiable method " + baseName);
		} catch (IllegalAccessException e) {
			Assert.fail(e.getMessage() + " problems creating new instance for method " + baseName);
		} catch (InstantiationException e) {
			Assert.fail(e.getMessage() + " problems creating new instance for method " + baseName);
		}
	}
	
	private void checkListModificationBlocked(Method getMethod, 
			Object testInstance) {
		// now, call the getter again
		List<?> returnList = 
			(List<?>)invokeMethod(getMethod, testInstance, (Object[])null);
		
		// now, try to add something to the collection and expect an exception
		try {
			returnList.add(null);
			Assert.fail(getMethod + " return should be unmodifiable");
		} catch (UnsupportedOperationException e) {
			// this is the expected behavior
			Assert.assertNotNull(e);
		}
	}

	private void checkMapModificationBlocked(Method getMethod, 
			Object testInstance) {
		// now, call the getter again
		Map<?, ?> returnMap = 
			(Map<?, ?>)invokeMethod(getMethod, testInstance, (Object[])null);
		
		// now, try to add something to the collection and expect an exception
		try {
			returnMap.put(null, null);
			Assert.fail(getMethod + " return should be unmodifiable");
		} catch (UnsupportedOperationException e) {
			// this is the expected behavior
			Assert.assertNotNull(e);
		}
	}
	
	private Object invokeMethod(Method method, Object testInstance, 
			Object[] params) {
		try {
			return method.invoke(testInstance, params);
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			
			if (cause instanceof RuntimeException) {
				throw (RuntimeException)cause;
			}
			Assert.fail(e.getClass().toString());
		} catch (IllegalArgumentException e) {
			Assert.fail(e.getClass().toString());
		} catch (IllegalAccessException e) {
			Assert.fail(e.getClass().toString());
		}
		return null;
	} 
}
