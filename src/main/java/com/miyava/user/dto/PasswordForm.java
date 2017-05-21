package com.miyava.user.dto;

public class PasswordForm {

    private String currentPassword;

    private String passwordOld;

    private String passwordConfirm;

    /**
     * @return the currentPassword
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * @param currentPassword the currentPassword to set
     */
    public void setCurrentPassword( String currentPassword ) {
        this.currentPassword = currentPassword;
    }

    /**
     * @return the passwordOld
     */
    public String getPasswordOld() {
        return passwordOld;
    }

    /**
     * @param passwordOld the passwordOld to set
     */
    public void setPasswordOld( String passwordOld ) {
        this.passwordOld = passwordOld;
    }

    /**
     * @return the passwordConfirm
     */
    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    /**
     * @param passwordConfirm the passwordConfirm to set
     */
    public void setPasswordConfirm( String passwordConfirm ) {
        this.passwordConfirm = passwordConfirm;
    }

}
