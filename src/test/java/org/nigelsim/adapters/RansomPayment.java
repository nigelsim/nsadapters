package org.nigelsim.adapters;

/**
 * Example adapted from http://martinaspeli.net/articles/a-java-component-architecture
 * @author nigel
 *
 */
public class RansomPayment implements IPayment {

    private Ransom ransom;

    public RansomPayment(Ransom ransom) {
        this.ransom = ransom;
    }

    public void pay() {
        ransom.setCash(100);
    }

}
