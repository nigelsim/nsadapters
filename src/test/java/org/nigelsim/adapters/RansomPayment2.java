package org.nigelsim.adapters;

/**
 * Example adapted from http://martinaspeli.net/articles/a-java-component-architecture
 * @author nigel
 *
 */
@Adapter(forClass=Ransom.class)
public class RansomPayment2 implements IPayment {

    private Ransom ransom;

    public RansomPayment2(Ransom ransom) {
        this.ransom = ransom;
    }

    public void pay() {
        ransom.setCash(200);
    }

}
