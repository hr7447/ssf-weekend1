package vttp.batch5.ssf.weekend1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import vttp.batch5.ssf.weekend1.model.loginForm;

@Controller
public class loginController {
    private static final String CORRECT_USERNAME = "hongrui";
    private static final String CORRECT_PASSWORD = "hr7474";
    private static final int MAX_ATTEMPTS = 2;

    @GetMapping("/")
    public String showLoginForm(Model model, HttpSession session) {
        if (session.getAttribute("loginAttempts") == null) {
            session.setAttribute("loginAttempts", 0);
        }
        model.addAttribute("loginForm", new loginForm());
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@ModelAttribute loginForm loginForm, HttpSession session, Model model) {
        Integer attempts = (Integer) session.getAttribute("loginAttempts");

        if (CORRECT_USERNAME.equals(loginForm.getUsername()) &&
                CORRECT_PASSWORD.equals(loginForm.getPassword())) {
            session.setAttribute("loginAttempts", 0);
            session.setAttribute("authenticated", true);
            return "redirect:/secret";
        }

        attempts++;
        session.setAttribute("loginAttempts", attempts);

        if (attempts > MAX_ATTEMPTS) {
            return "redirect:/captcha";
        }

        model.addAttribute("error", "Please try again");
        return "login";
    }

    @GetMapping("/captcha")
    public String showCaptchaForm(Model model) {
        model.addAttribute("loginForm", new loginForm());
        return "captcha";
    }

    @PostMapping("/captcha-login")
    public String processCaptchaLogin(@ModelAttribute loginForm loginForm, HttpSession session) {
        if (CORRECT_USERNAME.equals(loginForm.getUsername()) &&
                CORRECT_PASSWORD.equals(loginForm.getPassword()) &&
                "VISA".equals(loginForm.getCaptcha())) {
            session.setAttribute("authenticated", true);
            return "redirect:/secret";
        }
        return "redirect:/locked";
    }

    @GetMapping("/secret")
    public String showSecret(HttpSession session) {
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        if (authenticated == null || !authenticated) {
            return "redirect:/";
        }
        return "secret";
    }

    @GetMapping("/locked")
    public String showLocked() {
        return "locked";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
