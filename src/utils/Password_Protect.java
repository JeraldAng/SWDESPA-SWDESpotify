package utils;

import java.security.MessageDigest;

public class Password_Protect {

    private static byte[] salty;

    public Password_Protect(){
        salty = Const.salt.getBytes();
        System.out.println(salty);
    }

    public String hashPassword(String password){
        StringBuilder sb = new StringBuilder();
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salty);
            byte hashedPassword[] = md.digest(password.getBytes());

            for(int i = 0; i < hashedPassword.length; i++) {
                sb.append(Integer.toString((hashedPassword[i] & 0xff) + 0x100, 16).substring(1));
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }

//    public String decryptPassword(String password){
//
//    }
}
