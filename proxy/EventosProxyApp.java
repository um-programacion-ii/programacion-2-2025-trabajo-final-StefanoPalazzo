@EnableFeignClients
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
    "com.stefanopalazzo.proxy",
    "com.stefanopalazzo.proxy.web.rest"
})
public class EventosproxyApp {

    public static void main(String[] args) {
        SpringApplication.run(EventosproxyApp.class, args);
    }
}

