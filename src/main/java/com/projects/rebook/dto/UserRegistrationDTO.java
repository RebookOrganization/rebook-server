package com.projects.rebook.dto;

import com.projects.rebook.service.FieldMatch;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@FieldMatch.List({
    @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match"),
//    @FieldMatch(first = "email", second = "confirmEmail", message = "The email fields must match")
})
public class UserRegistrationDTO {
  @NotEmpty
  private String fullName;

  @NotEmpty
  private String password;

  @NotEmpty
  private String confirmPassword;

  @Email
  @NotEmpty
  private String email;

  @AssertTrue(message = "Vui lòng đồng ý với điều khoản của Rebook.com.vn")
  private Boolean terms;

  public String getFullName() { return fullName; }

  public void setFullName(String fullName) { this.fullName = fullName; }

  public String getPassword() { return password; }

  public void setPassword(String password) { this.password = password; }

  public String getConfirmPassword() { return confirmPassword; }

  public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

  public String getEmail() { return email; }

  public void setEmail(String email) { this.email = email; }

  public Boolean getTerms() { return terms; }

  public void setTerms(Boolean terms) { this.terms = terms; }

}
