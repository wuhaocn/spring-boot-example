* spring-boot-web-boot

```
public class StarterMultiApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(StarterMultiApplication.class);

    public static void main(String[] args) {

        SpringApplication.run(StarterS1Application.class, mergeArgs("--spring.profiles.active=s1", args));

        SpringApplication.run(StarterS2Application.class, mergeArgs("--spring.profiles.active=s2", args));
    }

    public static String[] mergeArgs(String arg, String[] args) {
        String[] retArgs = new String[args.length + 1];
        retArgs[0] = arg;
        for (int i = 1; i < retArgs.length; i++) {
            retArgs[i] = args[i - 1];
        }
        return retArgs;
    }
}

```