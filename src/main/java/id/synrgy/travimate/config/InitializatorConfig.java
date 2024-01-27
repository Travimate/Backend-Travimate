//package id.synrgy.travimate.config;
//
//@Configuration
//public class InitializatorConfig {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Value("${app.firebase-configuration-file}")
//    private String firebaseConfigPath;
//    Logger logger = LoggerFactory.getLogger(InitializatorConfig.class);
//
//    @PostConstruct
//    public void initializeDatabase() {
//        Role.initializeRoles(roleRepository);
//    }
//
//}