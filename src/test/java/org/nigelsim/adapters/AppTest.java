package org.nigelsim.adapters;

import java.lang.reflect.InvocationTargetException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 * 
	 * @param testName
	 *            name of the test case
	 */
	public AppTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	/**
	 * Example adapted from
	 * http://martinaspeli.net/articles/a-java-component-architecture
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NotAnAdapterException 
	 */
	public void testExplicitAdapter() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NotAnAdapterException {
		AdapterManager.getInstance().register(IPayment.class, Ransom.class,
				RansomPayment.class);
		Ransom ransom = new Ransom();
		ransom.setCash(0);

		IPayment payment = (IPayment) AdapterManager.getInstance().adapt(
				IPayment.class, ransom);
		payment.pay();

		assertEquals(100, ransom.getCash());
	}
	
	/**
	 * Example adapted from
	 * http://martinaspeli.net/articles/a-java-component-architecture
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws NotAnAdapterException 
	 */
	public void testAnnotation() throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, NotAnAdapterException {
		AdapterManager.getInstance().register(RansomPayment2.class);
		Ransom ransom = new Ransom();
		ransom.setCash(0);

		IPayment payment = (IPayment) AdapterManager.getInstance().adapt(
				IPayment.class, ransom);
		payment.pay();

		assertEquals(200, ransom.getCash());
	}
}
