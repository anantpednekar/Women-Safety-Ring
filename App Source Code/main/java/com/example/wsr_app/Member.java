package com.example.wsr_app;

public class Member {

    public String Name;
    public String Email;
    public String Password;
    public String Phone;
    public String Address;
    public String Pincode;

    public Member() {

    }

    public Member(String name, String email, String password, String phone, String address, String pincode) {
        Name = name;
        Email = email;
        Password = password;
        Phone = phone;
        Address = address;
        Pincode = pincode;
    }
}

 /*   public String getName() { return Name; }
    public void setName(String name) { Name = name; }

    public String getEmail() { return Email; }
    public void setEmail(String email) { Email = email; }

    public String getPassword() { return Password; }
    public void setPassword(String password) { Password = password; }

    public String getPhone() { return Phone; }
    public void setPhone(String phone) { Phone = phone; }

    public String getAddress(String trim) { return Address; }
    public void setAddress(String address) { Address = address; }

    public String getPincode(String trim) { return Pincode; }
    public void setPincode(String pincode) { Pincode = pincode; }

    public String getUserId(String trim) { return UserId; }
    public void setUserId(String userId) { Pincode = userId; }
 }

*/



