package com.mku.order.helper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "Payments")
@XmlAccessorType(XmlAccessType.FIELD)
public class PaymentsXML {

    @XmlElement(name = "Payment")
    private List<PaymentInfo> paymentList;

    public PaymentsXML() {
        // Default constructor for JAXB
    }

    public PaymentsXML(List<PaymentInfo> paymentList) {
        this.paymentList = paymentList;
    }

    public List<PaymentInfo> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<PaymentInfo> paymentList) {
        this.paymentList = paymentList;
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
