package nio03.router;

import java.util.List;
import java.util.Random;

public class RandomHttpEndpointRouter implements HttpEndpointRouter {
    @Override
    public String route(List<String> urls) {
        int size = urls.size();
        var random = new Random(System.currentTimeMillis());
        return urls.get(random.nextInt(size));
    }
}
