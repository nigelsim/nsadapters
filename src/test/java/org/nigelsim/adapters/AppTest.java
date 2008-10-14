/**
 *  This file is part of nsAdapters.
 *
 *  nsAdapters is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  nsAdapters is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with nsAdapters.  If not, see <http://www.gnu.org/licenses/>.
 */
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
