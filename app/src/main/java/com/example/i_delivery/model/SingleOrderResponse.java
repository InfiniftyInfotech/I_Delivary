package com.example.i_delivery.model;

import java.util.List;

public class SingleOrderResponse {
    List<Product> result;
    String message;
    int response;
    String order_id;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public List<Product> getResult() {
        return result;
    }

    public void setResult(List<Product> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "ProductResponse{" +
                "result=" + result +
                ", message='" + message + '\'' +
                ", response=" + response +
                '}';
    }

    public class Product{
        private String code, product_name, qty, price, photo, attribute;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getProduct_name() {
            return product_name;
        }

        public void setProduct_name(String product_name) {
            this.product_name = product_name;
        }

        public String getQty() {
            return qty;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "code='" + code + '\'' +
                    ", product_name='" + product_name + '\'' +
                    ", qty='" + qty + '\'' +
                    ", price='" + price + '\'' +
                    ", photo='" + photo + '\'' +
                    ", attribute='" + attribute + '\'' +
                    '}';
        }
    }
}
