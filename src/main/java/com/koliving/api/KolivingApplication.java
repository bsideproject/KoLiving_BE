package com.koliving.api;

import static com.koliving.api.location.domain.LocationType.DONG;
import static com.koliving.api.location.domain.LocationType.GU;

import com.google.common.collect.Sets;
import com.koliving.api.file.domain.ImageFile;
import com.koliving.api.file.infra.ImageFileRepository;
import com.koliving.api.i18n.Language;
import com.koliving.api.i18n.LanguageRepository;
import com.koliving.api.location.domain.Location;
import com.koliving.api.location.infra.LocationRepository;
import com.koliving.api.room.domain.Furnishing;
import com.koliving.api.room.domain.FurnishingType;
import com.koliving.api.room.domain.Like;
import com.koliving.api.room.domain.Maintenance;
import com.koliving.api.room.domain.Money;
import com.koliving.api.room.domain.Room;
import com.koliving.api.room.domain.RoomType;
import com.koliving.api.room.domain.info.RoomInfo;
import com.koliving.api.room.infra.FurnishingRepository;
import com.koliving.api.room.infra.LikeRepository;
import com.koliving.api.room.infra.RoomRepository;
import com.koliving.api.user.User;
import com.koliving.api.user.UserRepository;
import com.koliving.api.user.UserRole;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@EnableJpaAuditing
@SpringBootApplication
@ConfigurationPropertiesScan("com.koliving.api.properties")
public class KolivingApplication {

    private static final String SAVED_FILE = "https://kr.object.ncloudstorage.com/backend-bucket/images/42202f9c-9f06-4199-b602-2e8fb85e5741";
    private static final String SAVED_FILE2 = "https://backend-bucket.kr.object.ncloudstorage.com/images/64ba2192-28ef-4210-b27d-dabc66fc511b";

    public static void main(String[] args) {
        SpringApplication.run(KolivingApplication.class, args);
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Profile("default")
    @Bean
    CommandLineRunner commandLineRunner(
        FurnishingRepository furnishingRepository,
        LocationRepository locationRepository,
        LanguageRepository languageRepository,
        RoomRepository roomRepository,
        ImageFileRepository imageFileRepository,
        LikeRepository likeRepository,
        UserRepository userRepository,
        PasswordEncoder encoder
    ) {
        return args -> {
            initImageFiles(imageFileRepository);
            initFurnishings(furnishingRepository);
            initLocations(locationRepository);
            initLanguages(languageRepository);
            User user = initUser(userRepository, encoder);
            initRooms(roomRepository, locationRepository, furnishingRepository, imageFileRepository, likeRepository, user);
        };
    }

    private User initUser(UserRepository userRepository, PasswordEncoder encoder) {
        final User user = User.valueOf("koliving@koliving.com", encoder.encode("test1234!@"), UserRole.USER);
        final User user2 = User.valueOf("koliving2@koliving.com", encoder.encode("test1234!@"), UserRole.USER);
        final User manager = User.valueOf("manager@koliving.com", encoder.encode("test1234!@"), UserRole.ADMIN);

        userRepository.saveAll(List.of(user2, manager));
        return userRepository.save(user);
    }

    private void initImageFiles(ImageFileRepository imageFileRepository) {
        imageFileRepository.saveAll(
            List.of(
                ImageFile.valueOf(SAVED_FILE, 3000L),
                ImageFile.valueOf(SAVED_FILE2, 2000L)
            )
        );
    }

    private void initRooms(RoomRepository roomRepository, LocationRepository locationRepository,
        FurnishingRepository furnishingRepository, ImageFileRepository imageFileRepository, LikeRepository likeRepository, User user) {
        Location location = locationRepository.findByName("Songjeong").get();
        Location location2 = locationRepository.findByName("Huam").get();
        Location location3 = locationRepository.findByName("Amsaje 1").get();
        ImageFile imageFile = imageFileRepository.findByPath(SAVED_FILE).get();
        ImageFile imageFile2 = imageFileRepository.findByPath(SAVED_FILE2).get();

        List<Furnishing> furnishings = furnishingRepository.findAll();

        Furnishing tv = furnishings.stream()
            .filter(it -> it.getType().isTV())
            .findFirst().get();

        Furnishing bed = furnishings.stream()
            .filter(it -> it.getType().isBed())
            .findFirst().get();

        roomRepository.saveAll(
            List.of(
                Room.valueOf(
                    location,
                    RoomInfo.valueOf(RoomType.STUDIO, 0, 1, 1),
                    Money.empty(),
                    Money.empty(),
                    Maintenance.empty(),
                    Sets.newHashSet(),
                    LocalDate.of(2023, 8, 29),
                    "성동구 송정동) STUDIO, 방1, 욕실1, 룸메1 보증금X 월세X 관리비X 가구X 2023.08.29 입주",
                    Sets.newHashSet(
                        imageFile,
                        imageFile2
                    )
                ).by(user),
                Room.valueOf(
                    location2,
                    RoomInfo.valueOf(RoomType.ONE_BED_FLATS, 1, 2, 2),
                    Money.valueOf(5_000_000),
                    Money.empty(),
                    Maintenance.empty(),
                    Sets.newHashSet(),
                    LocalDate.of(2023, 8, 30),
                    "용산구 후암동) ONE_BED_FLATS, 방1, 욕실2, 룸메2 보증금 5_000_000 월세X 관리비X 가구X 2023.08.30 입주",
                    Sets.newHashSet()
                ).by(user),
                Room.valueOf(
                    location3,
                    RoomInfo.valueOf(RoomType.ONE_BED_FLATS, 1, 2, 2),
                    Money.valueOf(5_000_000),
                    Money.valueOf(300_000),
                    Maintenance.empty(),
                    Sets.newHashSet(tv, bed),
                    LocalDate.now(),
                    "강동구 암사제1동) ONE_BED_FLATS, 방1, 욕실2, 룸메2 보증금 5_000_000 월세300_000 관리비X 가구X 2023.08.30 입주",
                    Sets.newHashSet()
                ).by(user)
            )
        );

        likeRepository.save(Like.of(roomRepository.findAll().get(0), user));
    }

    private void initLanguages(LanguageRepository languageRepository) {
        languageRepository.saveAll(
            List.of(
                Language.valueOf("en", "home_welcome", "Welcome"),
                Language.valueOf("ko", "home_welcome", "환영합니다"),

                Language.valueOf("en", "email_duplication", "This email already exists : {0}"),
                Language.valueOf("ko", "email_duplication", "이미 존재하는 이메일입니다 : {0}"),
                Language.valueOf("en", "auth_email_subject", "Confirm your email"),
                Language.valueOf("ko", "auth_email_subject", "이메일 인증을 완료하세요"),
                Language.valueOf("en", "auth_email_subtitle", "Click the link below to proceed with authentication"),
                Language.valueOf("ko", "auth_email_subtitle", "아래 링크를 클릭하셔서 인증을 진행하세요"),
                Language.valueOf("en", "auth_email_link_guidance",
                    "If the button doesn't work, copy and paste the link below into url"),
                Language.valueOf("ko", "auth_email_link_guidance", "버튼이 동작하지 않다면, 아래 링크를 url에 붙여 인증을 시도하세요"),
                Language.valueOf("en", "expired_confirmation_token", "The confirmation token has expired"),
                Language.valueOf("ko", "expired_confirmation_token", "만료된 확인 토큰입니다"),
                Language.valueOf("en", "authenticated_confirmation_token", "The confirmation token already confirmed"),
                Language.valueOf("ko", "authenticated_confirmation_token token", "이미 인증된 확인 토큰입니다"),
                Language.valueOf("en", "ungenerated_confirmation_token",
                    "The confirmation token is not a normal generated by the server"),
                Language.valueOf("ko", "ungenerated_confirmation_token", "서버에 의해 생성된 정상 확인 토큰이 아닙니다"),

                Language.valueOf("en", "login_exception", "The email or password you requested is invalid"),
                Language.valueOf("ko", "login_exception", "이메일 혹은 비밀번호가 유효하지 않습니다"),

                Language.valueOf("en", "not_found_auth_header", "Authorization header not found"),
                Language.valueOf("ko", "not_found_auth_header", "인증 헤더에 토큰 정보가 없습니다"),
                Language.valueOf("en", "not_bearer_type", "Authorization header does not start with Bearer"),
                Language.valueOf("ko", "not_bearer_type", "인증 헤더의 타입은 \"Bearer\" 입니다"),
                Language.valueOf("en", "expired_token", "Access token has expired"),
                Language.valueOf("ko", "expired_token", "만료된 엑세스 토큰입니다"),
                Language.valueOf("en", "malformed_token", "Malformed jwt token"),
                Language.valueOf("ko", "malformed_token", "잘못된 형식의 액세스 토큰입니다"),
                Language.valueOf("en", "signature_invalid_token", "Signature validation failed"),
                Language.valueOf("ko", "signature_invalid_token", "인증부에서 검증 실패"),
                Language.valueOf("en", "format_invalid_token", "Unexpected format of jwt token"),
                Language.valueOf("ko", "format_invalid_token", "지원하지 않는 형식의 토큰입니다"),

                Language.valueOf("en", "Access Denied", "Unauthorized request"),
                Language.valueOf("ko", "Access Denied", "권한이 없는 요청입니다"),

                Language.valueOf("en", "email_not_exists", "This email doesn't exist : {0}"),
                Language.valueOf("ko", "email_not_exists", "존재하지 않는 이메일입니다 : {0}"),

                Language.valueOf("en", "mismatched_password", "The Password and verification password does not match"),
                Language.valueOf("ko", "mismatched_password", "비밀번호가 확인 비밀번호와 일치하지 않습니다"),

                Language.valueOf("en", "invalid_password", "The Password is not valid"),
                Language.valueOf("ko", "invalid_password", "유효하지 않은 비밀번호입니다."),

                Language.valueOf("en", "black_list_token", "This token is blacklisted"),
                Language.valueOf("ko", "black_list_token", "이 토큰은 블랙리스트에 등록되어 있습니다")
            )
        );

    }

    private void initFurnishings(FurnishingRepository furnishingRepository) {
        final List<Furnishing> furnishings = Arrays.stream(FurnishingType.values())
            .map(Furnishing::valueOf)
            .collect(Collectors.toList());

        furnishingRepository.saveAll(furnishings);
    }

    private void initLocations(LocationRepository locationRepository) {
        saveJongno(locationRepository);
        saveJung(locationRepository);
        saveGwangjin(locationRepository);
        saveDongdaemun(locationRepository);
        saveJungnang(locationRepository);
        saveSeongbuk(locationRepository);
        saveYongsan(locationRepository);
        saveSeongdong(locationRepository);
        saveGwangjin(locationRepository);
        saveGangbuk(locationRepository);
        saveDobong(locationRepository);
        saveNowon(locationRepository);
        saveEunpyeong(locationRepository);
        saveSeodamun(locationRepository);
        saveMapo(locationRepository);
        saveYangcheon(locationRepository);
        saveGangseo(locationRepository);
        saveGuro(locationRepository);
        saveGeumcheon(locationRepository);
        saveYeongdeungpo(locationRepository);
        saveDongjak(locationRepository);
        saveGwanak(locationRepository);
        saveSeocho(locationRepository);
        saveGangnam(locationRepository);
        saveSongpa(locationRepository);
        saveGangdong(locationRepository);
    }

    private void saveGangdong(LocationRepository locationRepository) {
        Location gangdong = locationRepository.save(Location.valueOf("Gangdong", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Gangil", DONG, gangdong),
                Location.valueOf("Sangil", DONG, gangdong),
                Location.valueOf("Myeongilje 1", DONG, gangdong),
                Location.valueOf("Myeongilje 2", DONG, gangdong),
                Location.valueOf("Godeokje 1", DONG, gangdong),
                Location.valueOf("Godeokje 2", DONG, gangdong),
                Location.valueOf("Amsaje 1", DONG, gangdong),
                Location.valueOf("Amsaje 2", DONG, gangdong),
                Location.valueOf("Amsaje 3", DONG, gangdong),
                Location.valueOf("Cheonhoje 1", DONG, gangdong),
                Location.valueOf("Cheonhoje 2", DONG, gangdong),
                Location.valueOf("Cheonhoje 3", DONG, gangdong),
                Location.valueOf("Seongnaeje 1", DONG, gangdong),
                Location.valueOf("Seongnaeje 2", DONG, gangdong),
                Location.valueOf("Seongnaeje 3", DONG, gangdong),
                Location.valueOf("Gil", DONG, gangdong),
                Location.valueOf("Dunchon 1", DONG, gangdong),
                Location.valueOf("Dunchon 2", DONG, gangdong)
            )
        );
    }

    private void saveSongpa(LocationRepository locationRepository) {
        Location songpa = locationRepository.save(Location.valueOf("Songpa", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Pungnap 1", DONG, songpa),
                Location.valueOf("Pungnap 2", DONG, songpa),
                Location.valueOf("Geoyeo 1", DONG, songpa),
                Location.valueOf("Geoyeo2", DONG, songpa),
                Location.valueOf("Macheon 1", DONG, songpa),
                Location.valueOf("Macheon 2", DONG, songpa),
                Location.valueOf("Bang-i 1", DONG, songpa),
                Location.valueOf("Bang-i 2", DONG, songpa),
                Location.valueOf("Oryun", DONG, songpa),
                Location.valueOf("Ogeum", DONG, songpa),
                Location.valueOf("Songpa 1", DONG, songpa),
                Location.valueOf("Songpa 2", DONG, songpa),
                Location.valueOf("Seokchon", DONG, songpa),
                Location.valueOf("Samjeon", DONG, songpa),
                Location.valueOf("Garakbon", DONG, songpa),
                Location.valueOf("Garak 1", DONG, songpa),
                Location.valueOf("Garak 2", DONG, songpa),
                Location.valueOf("Munjeong 1", DONG, songpa),
                Location.valueOf("Munjeong 2", DONG, songpa),
                Location.valueOf("Jangji", DONG, songpa),
                Location.valueOf("Wirye", DONG, songpa),
                Location.valueOf("Jamsil bon", DONG, songpa),
                Location.valueOf("Jamsil 2", DONG, songpa),
                Location.valueOf("Jamsil 3", DONG, songpa),
                Location.valueOf("Jamsil 4", DONG, songpa),
                Location.valueOf("Jamsil 6", DONG, songpa),
                Location.valueOf("Jamsil 7", DONG, songpa)

            )
        );
    }

    private void saveGangnam(LocationRepository locationRepository) {
        Location gangnam = locationRepository.save(Location.valueOf("Gangnam", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Sinsa", DONG, gangnam),
                Location.valueOf("Nonhyeon 1", DONG, gangnam),
                Location.valueOf("Nonhyeon 2", DONG, gangnam),
                Location.valueOf("Apgujeong", DONG, gangnam),
                Location.valueOf("Cheongdam", DONG, gangnam),
                Location.valueOf("Samsung 1", DONG, gangnam),
                Location.valueOf("Samsung 2", DONG, gangnam),
                Location.valueOf("Daechi 1", DONG, gangnam),
                Location.valueOf("Daechi 2", DONG, gangnam),
                Location.valueOf("Daechi 4", DONG, gangnam),
                Location.valueOf("Yeoksam 1", DONG, gangnam),
                Location.valueOf("Yeoksam 2", DONG, gangnam),
                Location.valueOf("Dogok 1", DONG, gangnam),
                Location.valueOf("Dogok 2", DONG, gangnam),
                Location.valueOf("Gaepo 1", DONG, gangnam),
                Location.valueOf("Gaepo 2", DONG, gangnam),
                Location.valueOf("Gaeppo 4", DONG, gangnam),
                Location.valueOf("Segok", DONG, gangnam),
                Location.valueOf("Ilwonbon dong", DONG, gangnam),
                Location.valueOf("Ilwon 1", DONG, gangnam),
                Location.valueOf("Ilwon 2", DONG, gangnam),
                Location.valueOf("Suseo", DONG, gangnam)
            )
        );
    }

    private void saveSeocho(LocationRepository locationRepository) {
        Location seocho = locationRepository.save(Location.valueOf("Seocho", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Seocho 1", DONG, seocho),
                Location.valueOf("Seocho 2", DONG, seocho),
                Location.valueOf("Seocho 3", DONG, seocho),
                Location.valueOf("Seocho 4", DONG, seocho),
                Location.valueOf("Jamwon", DONG, seocho),
                Location.valueOf("Banpobon", DONG, seocho),
                Location.valueOf("Banpo 1", DONG, seocho),
                Location.valueOf("Banpo 2", DONG, seocho),
                Location.valueOf("Banpo 3", DONG, seocho),
                Location.valueOf("Banpo 4", DONG, seocho),
                Location.valueOf("Bangbaebon", DONG, seocho),
                Location.valueOf("Bangbae 1", DONG, seocho),
                Location.valueOf("Bangbae 2", DONG, seocho),
                Location.valueOf("Bangbae 3", DONG, seocho),
                Location.valueOf("Bangbae 4", DONG, seocho),
                Location.valueOf("Yangjae1", DONG, seocho),
                Location.valueOf("Yangjae2", DONG, seocho),
                Location.valueOf("Naegok", DONG, seocho)
            )
        );
    }

    private void saveGwanak(LocationRepository locationRepository) {
        Location gwanak = locationRepository.save(Location.valueOf("Gwanak", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Boramae", DONG, gwanak),
                Location.valueOf("Cheonglim", DONG, gwanak),
                Location.valueOf("Seonghyeon", DONG, gwanak),
                Location.valueOf("haengun dong", DONG, gwanak),
                Location.valueOf("Nakseongdae", DONG, gwanak),
                Location.valueOf("Cheongnyong", DONG, gwanak),
                Location.valueOf("Euncheon", DONG, gwanak),
                Location.valueOf("Jungang", DONG, gwanak),
                Location.valueOf("Inheon", DONG, gwanak),
                Location.valueOf("Namhyeon", DONG, gwanak),
                Location.valueOf("Seowon", DONG, gwanak),
                Location.valueOf("Sinwon", DONG, gwanak),
                Location.valueOf("Seorim", DONG, gwanak),
                Location.valueOf("Sinsa", DONG, gwanak),
                Location.valueOf("Sillim", DONG, gwanak),
                Location.valueOf("Nanyang", DONG, gwanak),
                Location.valueOf("Jowon", DONG, gwanak),
                Location.valueOf("Daehak", DONG, gwanak),
                Location.valueOf("Samseong", DONG, gwanak),
                Location.valueOf("Miseong dong", DONG, gwanak),
                Location.valueOf("Nangok", DONG, gwanak)
            )
        );
    }

    private void saveDongjak(LocationRepository locationRepository) {
        Location dongjak = locationRepository.save(Location.valueOf("Dongjak", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Noryangjinje 1", DONG, dongjak),
                Location.valueOf("Noryangjinje 2", DONG, dongjak),
                Location.valueOf("Sangdoje 1", DONG, dongjak),
                Location.valueOf("Sangdoje 2", DONG, dongjak),
                Location.valueOf("Sangdoje 3", DONG, dongjak),
                Location.valueOf("Sangdoje 4", DONG, dongjak),
                Location.valueOf("Heugseog dong", DONG, dongjak),
                Location.valueOf("Sadangje 1", DONG, dongjak),
                Location.valueOf("Sadangje 2", DONG, dongjak),
                Location.valueOf("Sadangje 3", DONG, dongjak),
                Location.valueOf("Sadangje 4", DONG, dongjak),
                Location.valueOf("Sadangje 5", DONG, dongjak),
                Location.valueOf("Daebang", DONG, dongjak),
                Location.valueOf("Sindaebangje 1", DONG, dongjak),
                Location.valueOf("Sindaebangje 2", DONG, dongjak)
            )
        );
    }

    private void saveYeongdeungpo(LocationRepository locationRepository) {
        Location yeongdeungpo = locationRepository.save(Location.valueOf("Yeongdeungpo", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Yeongdeungpo main", DONG, yeongdeungpo),
                Location.valueOf("Yeongdeungpo", DONG, yeongdeungpo),
                Location.valueOf("Yeoui", DONG, yeongdeungpo),
                Location.valueOf("Dangsanje 1", DONG, yeongdeungpo),
                Location.valueOf("Dangsanje 2", DONG, yeongdeungpo),
                Location.valueOf("Dorim", DONG, yeongdeungpo),
                Location.valueOf("Munrae", DONG, yeongdeungpo),
                Location.valueOf("Yangpyeongje 1", DONG, yeongdeungpo),
                Location.valueOf("Yangpyeongje 2", DONG, yeongdeungpo),
                Location.valueOf("Shingilje 1", DONG, yeongdeungpo),
                Location.valueOf("Shingilje 3", DONG, yeongdeungpo),
                Location.valueOf("Shingilje 4", DONG, yeongdeungpo),
                Location.valueOf("Shingilje 5", DONG, yeongdeungpo),
                Location.valueOf("Shingilje 6", DONG, yeongdeungpo),
                Location.valueOf("Shingilje 7", DONG, yeongdeungpo),
                Location.valueOf("Daerimje 1", DONG, yeongdeungpo),
                Location.valueOf("Daerimje 2", DONG, yeongdeungpo),
                Location.valueOf("Daelimje 3", DONG, yeongdeungpo)
            )
        );
    }

    private void saveGeumcheon(LocationRepository locationRepository) {
        Location geumcheon = locationRepository.save(Location.valueOf("Geumcheon", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Gasan", DONG, geumcheon),
                Location.valueOf("Doksanje 1", DONG, geumcheon),
                Location.valueOf("Doksanje 2", DONG, geumcheon),
                Location.valueOf("Doksanje 3", DONG, geumcheon),
                Location.valueOf("Doksanje 4", DONG, geumcheon),
                Location.valueOf("Siheungje 1", DONG, geumcheon),
                Location.valueOf("Siheungje 2", DONG, geumcheon),
                Location.valueOf("Siheungje 3", DONG, geumcheon),
                Location.valueOf("Siheungje 4", DONG, geumcheon),
                Location.valueOf("Siheungje 5", DONG, geumcheon)
            )
        );
    }

    private void saveGuro(LocationRepository locationRepository) {
        Location guro = locationRepository.save(Location.valueOf("Guro", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Sindorim", DONG, guro),
                Location.valueOf("Guroje 1", DONG, guro),
                Location.valueOf("Guroje 2", DONG, guro),
                Location.valueOf("Guroje 3", DONG, guro),
                Location.valueOf("Guroje 4", DONG, guro),
                Location.valueOf("Guroje 5", DONG, guro),
                Location.valueOf("Garibong", DONG, guro),
                Location.valueOf("Gocheokje 1", DONG, guro),
                Location.valueOf("Gocheokje 2", DONG, guro),
                Location.valueOf("Gaebongje 1", DONG, guro),
                Location.valueOf("Gaebongje 2", DONG, guro),
                Location.valueOf("Gaebongje 3", DONG, guro),
                Location.valueOf("olyuje 1", DONG, guro),
                Location.valueOf("olyuje 2", DONG, guro),
                Location.valueOf("Sugung", DONG, guro)
            )
        );
    }

    private void saveGangseo(LocationRepository locationRepository) {
        Location gangseo = locationRepository.save(Location.valueOf("Gangseo", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Yeomchang", DONG, gangseo),
                Location.valueOf("Dalseongje 1", DONG, gangseo),
                Location.valueOf("Dalseongje 2", DONG, gangseo),
                Location.valueOf("Dalseongje 3", DONG, gangseo),
                Location.valueOf("Hwagokje 1", DONG, gangseo),
                Location.valueOf("Hwagokje 2", DONG, gangseo),
                Location.valueOf("Hwagokje 3", DONG, gangseo),
                Location.valueOf("Hwagokje 4", DONG, gangseo),
                Location.valueOf("Hwagokbon", DONG, gangseo),
                Location.valueOf("Hwagokje 6", DONG, gangseo),
                Location.valueOf("Hwagokje 8", DONG, gangseo),
                Location.valueOf("Gaeyangje 1", DONG, gangseo),
                Location.valueOf("Gaeyangje 2", DONG, gangseo),
                Location.valueOf("Gaeyangje 3", DONG, gangseo),
                Location.valueOf("Balsanje 1", DONG, gangseo),
                Location.valueOf("Wujangshan", DONG, gangseo),
                Location.valueOf("Gonghang", DONG, gangseo),
                Location.valueOf("Banghwaje 1", DONG, gangseo),
                Location.valueOf("Bangwhaje 2", DONG, gangseo),
                Location.valueOf("Bangwhaje 3", DONG, gangseo)
            )
        );
    }

    private void saveYangcheon(LocationRepository locationRepository) {
        Location yangcheon = locationRepository.save(Location.valueOf("Yangcheon", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Mok1", DONG, yangcheon),
                Location.valueOf("Mok2", DONG, yangcheon),
                Location.valueOf("Mok3", DONG, yangcheon),
                Location.valueOf("Mok4", DONG, yangcheon),
                Location.valueOf("Mok5", DONG, yangcheon),
                Location.valueOf("Shinyueol 1", DONG, yangcheon),
                Location.valueOf("Shinyueol 2", DONG, yangcheon),
                Location.valueOf("Shinyueol 3", DONG, yangcheon),
                Location.valueOf("Shinyueol 4", DONG, yangcheon),
                Location.valueOf("Shinyueol 5", DONG, yangcheon),
                Location.valueOf("Shinyueol 6", DONG, yangcheon),
                Location.valueOf("Shinyueol 7", DONG, yangcheon),
                Location.valueOf("Shinjeong 1", DONG, yangcheon),
                Location.valueOf("Shinjeong 2", DONG, yangcheon),
                Location.valueOf("Shinjeong 3", DONG, yangcheon),
                Location.valueOf("Shinjeong 4", DONG, yangcheon),
                Location.valueOf("Shinjeong 6", DONG, yangcheon),
                Location.valueOf("Shinjeong 7", DONG, yangcheon)
            )
        );
    }

    private void saveMapo(LocationRepository locationRepository) {
        Location mapo = locationRepository.save(Location.valueOf("Mapo", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Ahyeon", DONG, mapo),
                Location.valueOf("Gongdeok", DONG, mapo),
                Location.valueOf("Dowhwa", DONG, mapo),
                Location.valueOf("Yonggang", DONG, mapo),
                Location.valueOf("Daeheung", DONG, mapo),
                Location.valueOf("Yeomri", DONG, mapo),
                Location.valueOf("Sinsudong", DONG, mapo),
                Location.valueOf("Seogang", DONG, mapo),
                Location.valueOf("Seogyo", DONG, mapo),
                Location.valueOf("Hapjeong", DONG, mapo),
                Location.valueOf("Mangwon 1", DONG, mapo),
                Location.valueOf("Mangwon 2nd Building", DONG, mapo),
                Location.valueOf("Yeonnam", DONG, mapo),
                Location.valueOf("Seongsanje 1", DONG, mapo),
                Location.valueOf("Seongsanje 2", DONG, mapo),
                Location.valueOf("Sangam", DONG, mapo)
            )
        );
    }

    private void saveSeodamun(LocationRepository locationRepository) {
        Location seodaemun = locationRepository.save(Location.valueOf("Seodaemun", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Cheon-yeon dong", DONG, seodaemun),
                Location.valueOf("Bukahyeon", DONG, seodaemun),
                Location.valueOf("Chunghyeon", DONG, seodaemun),
                Location.valueOf("Sinchon", DONG, seodaemun),
                Location.valueOf("Yeonhee", DONG, seodaemun),
                Location.valueOf("Hongjeje 1", DONG, seodaemun),
                Location.valueOf("Hongjeje 3", DONG, seodaemun),
                Location.valueOf("Hongjeje 2", DONG, seodaemun),
                Location.valueOf("Hongeunje 1", DONG, seodaemun),
                Location.valueOf("Hongeunje 2", DONG, seodaemun),
                Location.valueOf("Namgajwaje 1", DONG, seodaemun),
                Location.valueOf("Namgajwaje 2", DONG, seodaemun),
                Location.valueOf("Bukgajaje 1", DONG, seodaemun),
                Location.valueOf("Bukgajajeong 2", DONG, seodaemun)
            )
        );
    }

    private void saveEunpyeong(LocationRepository locationRepository) {
        Location eunpyeong = locationRepository.save(Location.valueOf("Eunpyeong", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Nokbun", DONG, eunpyeong),
                Location.valueOf("Bulgwangje 1", DONG, eunpyeong),
                Location.valueOf("Bulgwangje 2", DONG, eunpyeong),
                Location.valueOf("Galhyeonje 1", DONG, eunpyeong),
                Location.valueOf("Galhyeonje 2", DONG, eunpyeong),
                Location.valueOf("Gusan", DONG, eunpyeong),
                Location.valueOf("Daejo", DONG, eunpyeong),
                Location.valueOf("Eungamje1", DONG, eunpyeong),
                Location.valueOf("Eungamje2", DONG, eunpyeong),
                Location.valueOf("Eungamje3", DONG, eunpyeong),
                Location.valueOf("Yeokchon", DONG, eunpyeong),
                Location.valueOf("Sinsaje 1", DONG, eunpyeong),
                Location.valueOf("Sinsaje 2", DONG, eunpyeong),
                Location.valueOf("Jeungsan dong", DONG, eunpyeong),
                Location.valueOf("Susaeg dong", DONG, eunpyeong),
                Location.valueOf("Jingguan", DONG, eunpyeong)
            )
        );
    }

    private void saveNowon(LocationRepository locationRepository) {
        Location nowon = locationRepository.save(Location.valueOf("Nowon", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Wolgye 1", DONG, nowon),
                Location.valueOf("Wolgye 2", DONG, nowon),
                Location.valueOf("Wolgye 3", DONG, nowon),
                Location.valueOf("Gongneung 1", DONG, nowon),
                Location.valueOf("Gongneung 2", DONG, nowon),
                Location.valueOf("Hagye 1", DONG, nowon),
                Location.valueOf("Hagye 2", DONG, nowon),
                Location.valueOf("Jungkyebon", DONG, nowon),
                Location.valueOf("Jungkye 1", DONG, nowon),
                Location.valueOf("Jungkye 4", DONG, nowon),
                Location.valueOf("Jungkye 2.3", DONG, nowon),
                Location.valueOf("Sang-gye 1", DONG, nowon),
                Location.valueOf("Sang-gye 2", DONG, nowon),
                Location.valueOf("Sang-gye 3.4", DONG, nowon),
                Location.valueOf("Sang-gye 5", DONG, nowon),
                Location.valueOf("Sang-gye 6.7 dong", DONG, nowon),
                Location.valueOf("Sang-gye 8 dong", DONG, nowon),
                Location.valueOf("Sang-gye 9 dong", DONG, nowon),
                Location.valueOf("Sang-gye 10 dong", DONG, nowon)
            )
        );
    }

    private void saveDobong(LocationRepository locationRepository) {
        Location dobong = locationRepository.save(Location.valueOf("Dobong", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Changje 1", DONG, dobong),
                Location.valueOf("Changje2", DONG, dobong),
                Location.valueOf("Changje 3", DONG, dobong),
                Location.valueOf("Changje 4", DONG, dobong),
                Location.valueOf("Changje 5", DONG, dobong),
                Location.valueOf("Dobongje 1", DONG, dobong),
                Location.valueOf("Dobongje 2", DONG, dobong),
                Location.valueOf("Ssangmunje 1", DONG, dobong),
                Location.valueOf("Ssangmunje 2", DONG, dobong),
                Location.valueOf("Ssangmunje 3", DONG, dobong),
                Location.valueOf("Ssangmunje 4", DONG, dobong),
                Location.valueOf("Banghakje 1", DONG, dobong),
                Location.valueOf("Banghakje 2", DONG, dobong),
                Location.valueOf("Banghakje 3", DONG, dobong)
            )
        );
    }

    private void saveGangbuk(LocationRepository locationRepository) {
        Location gangbuk = locationRepository.save(Location.valueOf("Gangbuk", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Samyang", DONG, gangbuk),
                Location.valueOf("Miadong", DONG, gangbuk),
                Location.valueOf("Songjungdong", DONG, gangbuk),
                Location.valueOf("Songcheon", DONG, gangbuk),
                Location.valueOf("Samkaksan", DONG, gangbuk),
                Location.valueOf("Byeon 1", DONG, gangbuk),
                Location.valueOf("Byeon 2", DONG, gangbuk),
                Location.valueOf("Byeon 3", DONG, gangbuk),
                Location.valueOf("Suyu 1", DONG, gangbuk),
                Location.valueOf("Suyu 2", DONG, gangbuk),
                Location.valueOf("Suyu 3", DONG, gangbuk),
                Location.valueOf("U-i dong", DONG, gangbuk),
                Location.valueOf("Insu", DONG, gangbuk)
            )
        );
    }

    private void saveSeongdong(LocationRepository locationRepository) {
        Location seongdong = locationRepository.save(Location.valueOf("Seongdong", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Wangsimlije 2", DONG, seongdong),
                Location.valueOf("Wangsimnidosun", DONG, seongdong),
                Location.valueOf("Majang", DONG, seongdong),
                Location.valueOf("Sagun", DONG, seongdong),
                Location.valueOf("Hangdangje 1", DONG, seongdong),
                Location.valueOf("Hangdangje 2", DONG, seongdong),
                Location.valueOf("Eungbong", DONG, seongdong),
                Location.valueOf("Kumho 1-ga", DONG, seongdong),
                Location.valueOf("Kumho 2.3-ga", DONG, seongdong),
                Location.valueOf("Kumho4 4-ga", DONG, seongdong),
                Location.valueOf("Oksu", DONG, seongdong),
                Location.valueOf("Seongsu 1-gaje 1", DONG, seongdong),
                Location.valueOf("Seongsu 1-gaje 2", DONG, seongdong),
                Location.valueOf("Seongsu 2-gaje 1", DONG, seongdong),
                Location.valueOf("Seongsu2gaje 3", DONG, seongdong),
                Location.valueOf("Songjeong", DONG, seongdong),
                Location.valueOf("Yongdap", DONG, seongdong)
            )
        );
    }

    private void saveYongsan(LocationRepository locationRepository) {
        Location yongsan = locationRepository.save(Location.valueOf("Yongsan", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Huam", DONG, yongsan),
                Location.valueOf("Yongsan 2(i)ga", DONG, yongsan),
                Location.valueOf("Namyeong", DONG, yongsan),
                Location.valueOf("Cheongpa", DONG, yongsan),
                Location.valueOf("Wonhyoro 1(il)", DONG, yongsan),
                Location.valueOf("Wonhyoro 2(i)", DONG, yongsan),
                Location.valueOf("Hyochang", DONG, yongsan),
                Location.valueOf("Yongmun", DONG, yongsan),
                Location.valueOf("Hangangno", DONG, yongsan),
                Location.valueOf("Ichon 1(il)", DONG, yongsan),
                Location.valueOf("Ichon 2(i)", DONG, yongsan),
                Location.valueOf("Itaewon 1(il)", DONG, yongsan),
                Location.valueOf("Itaewon 2(i)", DONG, yongsan),
                Location.valueOf("Hannam", DONG, yongsan),
                Location.valueOf("Seobingodong", DONG, yongsan),
                Location.valueOf("Bogwang", DONG, yongsan)
            )
        );
    }

    private void saveSeongbuk(LocationRepository locationRepository) {
        Location seongbuk = locationRepository.save(Location.valueOf("Seongbuk", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Seongbukdong", DONG, seongbuk),
                Location.valueOf("Samsun", DONG, seongbuk),
                Location.valueOf("Dongseon", DONG, seongbuk),
                Location.valueOf("Donamje 1", DONG, seongbuk),
                Location.valueOf("Donamje 2", DONG, seongbuk),
                Location.valueOf("Anam", DONG, seongbuk),
                Location.valueOf("Bomun", DONG, seongbuk),
                Location.valueOf("Jeongneungje 1", DONG, seongbuk),
                Location.valueOf("Jeongneungje 2", DONG, seongbuk),
                Location.valueOf("Jeongneungje 3", DONG, seongbuk),
                Location.valueOf("Jeongneungje 4", DONG, seongbuk),
                Location.valueOf("Gilumje 1", DONG, seongbuk),
                Location.valueOf("Gilumje2", DONG, seongbuk),
                Location.valueOf("Jongam", DONG, seongbuk),
                Location.valueOf("Wolgokje 1", DONG, seongbuk),
                Location.valueOf("Wolgokje 2", DONG, seongbuk),
                Location.valueOf("Jangwije 1", DONG, seongbuk),
                Location.valueOf("Jangwije2", DONG, seongbuk),
                Location.valueOf("Jangwijeong 3", DONG, seongbuk),
                Location.valueOf("Seokgwan", DONG, seongbuk)
            )
        );
    }

    private void saveJungnang(LocationRepository locationRepository) {
        Location jungnang = locationRepository.save(Location.valueOf("Jungnang", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Myeonmokje 2", DONG, jungnang),
                Location.valueOf("Myeonmokje 4", DONG, jungnang),
                Location.valueOf("Myeonmokje 5", DONG, jungnang),
                Location.valueOf("Myeonmok bon ", DONG, jungnang),
                Location.valueOf("Myeonmokje 7", DONG, jungnang),
                Location.valueOf("Myeonmokje 3.8", DONG, jungnang),
                Location.valueOf("Sangbongje 1", DONG, jungnang),
                Location.valueOf("Sangbongje 2", DONG, jungnang),
                Location.valueOf("junghwaje 1", DONG, jungnang),
                Location.valueOf("junghwaje 2", DONG, jungnang),
                Location.valueOf("Mukje 1", DONG, jungnang),
                Location.valueOf("Mukje2", DONG, jungnang),
                Location.valueOf("Mangwoobondong", DONG, jungnang),
                Location.valueOf("Mangwooje3dong", DONG, jungnang),
                Location.valueOf("Sinnae 1", DONG, jungnang),
                Location.valueOf("Sinnae 2", DONG, jungnang)
            )
        );
    }

    private void saveDongdaemun(LocationRepository locationRepository) {
        Location dongdaemun = locationRepository.save(Location.valueOf("Dongdaemun", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Yongshin", DONG, dongdaemun),
                Location.valueOf("Jaejeong", DONG, dongdaemun),
                Location.valueOf("Jeonnongje 1", DONG, dongdaemun),
                Location.valueOf("Jeonnongje 2", DONG, dongdaemun),
                Location.valueOf("Dapseoprije 1", DONG, dongdaemun),
                Location.valueOf("Dapsiprije 2", DONG, dongdaemun),
                Location.valueOf("Changan 1", DONG, dongdaemun),
                Location.valueOf("Janganje 2", DONG, dongdaemun),
                Location.valueOf("Cheongnyang-ri", DONG, dongdaemun),
                Location.valueOf("Hoegi", DONG, dongdaemun),
                Location.valueOf("Hwigyeongje 1", DONG, dongdaemun),
                Location.valueOf("Hwigyeongje 2", DONG, dongdaemun),
                Location.valueOf("Imunje 1", DONG, dongdaemun),
                Location.valueOf("Imunje 2", DONG, dongdaemun)
            )
        );
    }

    private void saveGwangjin(LocationRepository locationRepository) {
        Location gwangjin = locationRepository.save(Location.valueOf("Gwangjin", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Hwayang", DONG, gwangjin),
                Location.valueOf("Gunja", DONG, gwangjin),
                Location.valueOf("Junggokje 1", DONG, gwangjin),
                Location.valueOf("Junggokje 2", DONG, gwangjin),
                Location.valueOf("Junggokje 3", DONG, gwangjin),
                Location.valueOf("Junggokje 4", DONG, gwangjin),
                Location.valueOf("Neung", DONG, gwangjin),
                Location.valueOf("Gwangjang", DONG, gwangjin),
                Location.valueOf("Jiyangje 1", DONG, gwangjin),
                Location.valueOf("Jiyangje 2", DONG, gwangjin),
                Location.valueOf("Jiyangje 3", DONG, gwangjin),
                Location.valueOf("Jiyangje 4", DONG, gwangjin),
                Location.valueOf("Guuije 1", DONG, gwangjin),
                Location.valueOf("Guuije 2", DONG, gwangjin),
                Location.valueOf("Guuije 3", DONG, gwangjin)
            )
        );

    }

    private void saveJongno(LocationRepository locationRepository) {
        Location jongno = locationRepository.save(Location.valueOf("Jongno", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Cheongunhyoja", DONG, jongno),
                Location.valueOf("Sajik", DONG, jongno),
                Location.valueOf("Samcheong", DONG, jongno),
                Location.valueOf("Buam", DONG, jongno),
                Location.valueOf("Pyongchang", DONG, jongno),
                Location.valueOf("Muak", DONG, jongno),
                Location.valueOf("Gyonam", DONG, jongno),
                Location.valueOf("Gahoe", DONG, jongno),
                Location.valueOf("Jongno 1.2.3.4", DONG, jongno),
                Location.valueOf("Jongno 5.6-gadong", DONG, jongno),
                Location.valueOf("Ehwa", DONG, jongno),
                Location.valueOf("Hyehwa", DONG, jongno),
                Location.valueOf("Changsinje1", DONG, jongno),
                Location.valueOf("Changsinje2", DONG, jongno),
                Location.valueOf("Changsinje3", DONG, jongno),
                Location.valueOf("Sunginje1", DONG, jongno),
                Location.valueOf("Sunginje2", DONG, jongno)
            )
        );
    }

    private void saveJung(LocationRepository locationRepository) {
        Location jung = locationRepository.save(Location.valueOf("Jung", GU));
        locationRepository.saveAll(
            List.of(
                Location.valueOf("Sogong", DONG, jung),
                Location.valueOf("HoeHyeon", DONG, jung),
                Location.valueOf("Myeong", DONG, jung),
                Location.valueOf("Pil", DONG, jung),
                Location.valueOf("Jangchung", DONG, jung),
                Location.valueOf("Gwanghui", DONG, jung),
                Location.valueOf("Euljiro", DONG, jung),
                Location.valueOf("Sindang", DONG, jung),
                Location.valueOf("Dasan", DONG, jung),
                Location.valueOf("Yaksu", DONG, jung),
                Location.valueOf("Cheonggu", DONG, jung),
                Location.valueOf("Sindang 5(o)", DONG, jung),
                Location.valueOf("Donghwa", DONG, jung),
                Location.valueOf("Hwanghak", DONG, jung),
                Location.valueOf("Jungnim", DONG, jung)
            )
        );
    }
}
