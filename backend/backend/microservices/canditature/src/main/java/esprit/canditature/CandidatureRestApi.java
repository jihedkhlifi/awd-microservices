package esprit.canditature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mic3/candidature")
public class CandidatureRestApi {
    @Value("${server.port}")
    private String port;

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from candidature on port " + port;
    }
}
