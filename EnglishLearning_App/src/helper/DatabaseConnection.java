package helper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.File;

public class DatabaseConnection {

    private static final String DB_FILENAME = "english_app.db";
    private static boolean initialized = false;

    private static String resolveUrl() {
        String[] paths = {
            DB_FILENAME,
            System.getProperty("user.dir") + File.separator + DB_FILENAME
        };
        for (String p : paths) {
            if (new File(p).exists()) return "jdbc:sqlite:" + new File(p).getAbsolutePath();
        }
        return "jdbc:sqlite:" + new File(System.getProperty("user.dir"), DB_FILENAME).getAbsolutePath();
    }

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(resolveUrl());
            if (!initialized) {
                createSchema(conn);
                seedData(conn);
                initialized = true;
            }
            return conn;
        } catch (Exception e) {
            System.err.println("Loi ket noi DB: " + e.getMessage());
            return null;
        }
    }

    private static void createSchema(Connection conn) throws Exception {
        try (Statement s = conn.createStatement()) {
            s.execute("CREATE TABLE IF NOT EXISTS Users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT NOT NULL UNIQUE," +
                "password TEXT NOT NULL," +
                "email TEXT UNIQUE," +
                "phone TEXT UNIQUE)");

            s.execute("CREATE TABLE IF NOT EXISTS Lessons (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "lesson_name TEXT NOT NULL," +
                "description TEXT)");

            s.execute("CREATE TABLE IF NOT EXISTS Vocabularies (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "word TEXT NOT NULL," +
                "type TEXT," +
                "meaning TEXT NOT NULL," +
                "pronunciation TEXT," +
                "example TEXT," +
                "lesson_id INTEGER," +
                "FOREIGN KEY (lesson_id) REFERENCES Lessons(id))");

            s.execute("CREATE TABLE IF NOT EXISTS UserProgress (" +
                "user_id INTEGER," +
                "vocabulary_id INTEGER," +
                "is_mastered INTEGER DEFAULT 0," +
                "is_favorite INTEGER DEFAULT 0," +
                "learn_count INTEGER DEFAULT 0," +
                "PRIMARY KEY (user_id, vocabulary_id)," +
                "FOREIGN KEY (user_id) REFERENCES Users(id)," +
                "FOREIGN KEY (vocabulary_id) REFERENCES Vocabularies(id))");

            s.execute("CREATE TABLE IF NOT EXISTS QuizHistory (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER DEFAULT 1," +
                "quiz_number INTEGER DEFAULT 1," +
                "score INTEGER," +
                "date_taken TEXT)");

            try { s.execute("ALTER TABLE Users ADD COLUMN email TEXT"); } catch (Exception ignored) {}
            try { s.execute("ALTER TABLE Users ADD COLUMN phone TEXT"); } catch (Exception ignored) {}
            try { s.execute("ALTER TABLE UserProgress ADD COLUMN learn_count INTEGER DEFAULT 0"); } catch (Exception ignored) {}
            try { s.execute("ALTER TABLE QuizHistory ADD COLUMN user_id INTEGER DEFAULT 1"); } catch (Exception ignored) {}
            try { s.execute("ALTER TABLE QuizHistory ADD COLUMN quiz_number INTEGER DEFAULT 1"); } catch (Exception ignored) {}
        }
    }

    private static void seedData(Connection conn) throws Exception {
        try (Statement s = conn.createStatement()) {
            try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM Lessons")) {
                if (rs.next() && rs.getInt(1) > 0) return;
            }

            s.execute("INSERT INTO Lessons (id,lesson_name,description) VALUES (1,'Cong so & Su nghiep','Business & Career')");
            s.execute("INSERT INTO Lessons (id,lesson_name,description) VALUES (2,'Du lich & Sinh ton','Travel & Survival')");
            s.execute("INSERT INTO Lessons (id,lesson_name,description) VALUES (3,'Cong nghe & Tuong lai','Tech & Future')");
            s.execute("INSERT INTO Lessons (id,lesson_name,description) VALUES (4,'Cam xuc & Giao tiep','Emotions & Socializing')");

            String[][] w1 = {
                {"Colleague","n","Dong nghiep","/kol.iig/","I get along well with my colleagues."},
                {"Negotiate","v","Dam phan","/ne.go.shi.eyt/","We need to negotiate a better deal."},
                {"Promotion","n","Su thang chuc","/pre.mo.shen/","She got a promotion last month."},
                {"Productivity","n","Nang suat","/prod.ek.tiv.e.ti/","Coffee increases my productivity."},
                {"Contract","n","Hop dong","/kon.trakt/","Please sign the contract here."},
                {"Interview","n","Cuoc phong van","/in.te.vyoo/","I have a job interview tomorrow."},
                {"Resume","n","So yeu ly lich","/rez.yoo.may/","Send your resume to the HR manager."},
                {"Salary","n","Tien luong","/sal.e.ri/","He earns a high salary."},
                {"Revenue","n","Doanh thu","/rev.en.yoo/","Company revenue grew by 20%."},
                {"Budget","n","Ngan sach","/buj.it/","We are on a tight budget."},
                {"Strategy","n","Chien luoc","/strat.e.ji/","We need a new marketing strategy."},
                {"Management","n","Su quan ly","/man.ij.ment/","Good management is key to success."},
                {"Leadership","n","Kha nang lanh dao","/lee.der.ship/","He has strong leadership skills."},
                {"Meeting","n","Cuoc hop","/mee.ting/","The meeting starts at 9 AM."},
                {"Presentation","n","Bai thuyet trinh","/prez.en.tay.shen/","Her presentation was excellent."},
                {"Report","n","Bao cao","/ri.port/","Please submit the report by Friday."},
                {"Investment","n","Su dau tu","/in.vest.ment/","Buying land is a good investment."},
                {"Client","n","Khach hang","/kly.ent/","We have a meeting with a client."},
                {"Customer","n","Nguoi mua hang","/kus.te.mer/","Customer satisfaction is our priority."},
                {"Partnership","n","Su hop tac","/part.ner.ship/","We entered into a partnership."},
                {"Startup","n","Cong ty khoi nghiep","/start.up/","He works for a tech startup."},
                {"Entrepreneur","n","Doanh nhan","/on.tre.pre.nur/","She is a successful entrepreneur."},
                {"Profit","n","Loi nhuan","/prof.it/","The company made a huge profit."},
                {"Loss","n","Thua lo","/los/","The business suffered a great loss."},
                {"Market","n","Thi truong","/mar.kit/","The housing market is booming."},
                {"Competitor","n","Doi thu canh tranh","/kem.pet.i.ter/","We must beat our competitors."},
                {"Feedback","n","Phan hoi","/feed.bak/","We value your honest feedback."},
                {"Signature","n","Chu ky","/sig.ne.cher/","Put your signature at the bottom."},
                {"Agreement","n","Thoa thuan","/e.gree.ment/","They reached an agreement."},
                {"Resign","v","Tu chuc","/ri.zyn/","He decided to resign from his job."}
            };
            insertWords(s, w1, 1);

            String[][] w2 = {
                {"Accommodation","n","Cho o","/e.kom.e.day.shen/","We booked our accommodation online."},
                {"Departure","n","Su khoi hanh","/di.par.cher/","Our departure is scheduled for 8 AM."},
                {"Itinerary","n","Lich trinh","/ay.tin.e.rer.i/","Let me check our travel itinerary."},
                {"Luggage","n","Hanh ly","/lug.ij/","Do not leave your luggage unattended."},
                {"Destination","n","Diem den","/des.ti.nay.shen/","Paris is a popular tourist destination."},
                {"Passport","n","Ho chieu","/pas.port/","You need a valid passport to travel."},
                {"Visa","n","Thi thuc","/vee.ze/","I applied for a tourist visa."},
                {"Flight","n","Chuyen bay","/flyt/","My flight was delayed by two hours."},
                {"Boarding","n","Len may bay","/bor.ding/","Boarding starts at gate 5."},
                {"Delay","v","Tri hoan","/di.lay/","The heavy rain caused a delay."},
                {"Souvenir","n","Do luu niem","/soo.ve.neer/","I bought a souvenir for my mom."},
                {"Sightseeing","n","Ngam canh","/syt.see.ing/","We went sightseeing in Rome."},
                {"Currency","n","Tien te","/kur.en.si/","The local currency is the Yen."},
                {"Exchange","v","Doi tien","/iks.chaynj/","Where can I exchange my money?"},
                {"Reservation","n","Su dat cho","/rez.er.vay.shen/","I have a reservation under the name John."},
                {"Reception","n","Quay le tan","/ri.sep.shen/","Leave your keys at the reception."},
                {"Backpack","n","Ba lo","/bak.pak/","He traveled across Europe with a backpack."},
                {"Guide","n","Huong dan vien","/gyd/","Our tour guide was very friendly."},
                {"Cruise","n","Chuyen du ngoan bien","/krooz/","They went on a luxury cruise."},
                {"Journey","n","Hanh trinh","/jur.ni/","Life is a journey."},
                {"Expedition","n","Cuoc tham hiem","/ek.spe.dish.en/","An expedition to the North Pole."},
                {"Altitude","n","Do cao","/al.ti.tyood/","We are flying at a high altitude."},
                {"Landmark","n","Dia danh noi bat","/land.mark/","The Eiffel Tower is a famous landmark."},
                {"Monument","n","Dai ky niem","/mon.yoo.ment/","A monument was built in his honor."},
                {"Scenery","n","Phong canh","/see.ne.ri/","The mountain scenery is breathtaking."},
                {"Map","n","Ban do","/map/","We looked at the map to find our way."},
                {"Compass","n","La ban","/kum.pes/","A compass points to the North."},
                {"Customs","n","Hai quan","/kus.temz/","We had to pass through customs."},
                {"Emergency","n","Truong hop khan cap","/i.mur.jen.si/","Call 911 in case of an emergency."},
                {"Transport","n","Giao thong","/trans.port/","Public transport here is very cheap."}
            };
            insertWords(s, w2, 2);

            String[][] w3 = {
                {"Algorithm","n","Thuat toan","/al.ge.ri.them/","YouTube uses a complex algorithm."},
                {"Innovation","n","Su doi moi","/in.e.vay.shen/","Apple is known for its innovation."},
                {"Database","n","Co so du lieu","/day.te.bays/","The user info is stored in a database."},
                {"Virtual","adj","Ao","/vur.choo.el/","Virtual reality is the future of gaming."},
                {"Cybersecurity","n","An ninh mang","/sy.ber.si.kyoor.e.ti/","Cybersecurity is crucial for banks."},
                {"Artificial","adj","Nhan tao","/ar.ti.fish.el/","Artificial intelligence is growing fast."},
                {"Hardware","n","Phan cung","/hard.wer/","Upgrading hardware improves performance."},
                {"Software","n","Phan mem","/soft.wer/","I need to install new software."},
                {"Network","n","Mang luoi","/net.wurk/","The computer network is down."},
                {"Server","n","May chu","/sur.ver/","The game server is currently offline."},
                {"Cloud","n","Dam may luu tru","/klowd/","All my photos are saved in the cloud."},
                {"Application","n","Ung dung","/ap.li.kay.shen/","This application is very useful."},
                {"Interface","n","Giao dien","/in.ter.fays/","The user interface is clean and simple."},
                {"Function","n","Chuc nang","/fungk.shen/","What is the function of this button?"},
                {"Variable","n","Bien so","/ver.i.e.bel/","Declare a variable before using it."},
                {"Framework","n","Khuon kho","/fraym.wurk/","Spring is a popular Java framework."},
                {"Deploy","v","Trien khai","/di.ploy/","We will deploy the app tomorrow."},
                {"Debug","v","Go loi","/dee.bug/","It took me hours to debug this code."},
                {"Optimize","v","Toi uu hoa","/op.ti.myz/","We need to optimize the database."},
                {"Encryption","n","Su ma hoa","/in.krip.shen/","End-to-end encryption keeps messages safe."},
                {"Automate","v","Tu dong hoa","/aw.te.mayt/","We should automate repetitive tasks."},
                {"Robotics","n","Nganh che tao robot","/ro.bot.iks/","He is studying robotics in college."},
                {"Blockchain","n","Chuoi khoi","/blok.chayn/","Bitcoin is based on blockchain technology."},
                {"Gadget","n","Thiet bi tien ich","/gaj.it/","He loves buying new tech gadgets."},
                {"Device","n","Thiet bi","/di.vys/","Please turn off your mobile devices."},
                {"Upgrade","v","Nang cap","/up.grayd/","I need to upgrade my laptop RAM."},
                {"Install","v","Cai dat","/in.stawl/","How do I install this program?"},
                {"Backup","n","Sao luu","/bak.up/","Always make a backup of your files."},
                {"Protocol","n","Giao thuc","/pro.te.kol/","HTTP is an internet protocol."},
                {"Code","n","Ma viet ma","/kod/","He writes Java code every day."}
            };
            insertWords(s, w3, 3);

            String[][] w4 = {
                {"Extrovert","n","Nguoi huong ngoai","/ek.stre.vurt/","As an extrovert, he loves going to parties."},
                {"Empathetic","adj","Dong cam","/em.pe.thet.ik/","She is a very empathetic listener."},
                {"Overwhelmed","adj","Choang ngop","/o.ver.welmd/","I feel overwhelmed by all this work."},
                {"Anxious","adj","Lo au","/angk.shes/","He was anxious about the test results."},
                {"Enthusiastic","adj","Nhiet huyet","/in.thyoo.zi.as.tik/","They are enthusiastic about the new project."},
                {"Introvert","n","Nguoi huong noi","/in.tre.vurt/","An introvert prefers quiet spaces."},
                {"Confident","adj","Tu tin","/kon.fi.dent/","She is confident in her abilities."},
                {"Insecure","adj","Thieu tu tin","/in.si.kyoor/","Teenagers often feel insecure."},
                {"Jealous","adj","Ghen ti","/jel.es/","Do not be jealous of his success."},
                {"Grateful","adj","Biet on","/grayt.fel/","I am grateful for your help."},
                {"Frustrated","adj","Buc boi","/frus.tray.tid/","I get frustrated when my code does not work."},
                {"Delighted","adj","Vui mung","/di.ly.tid/","We are delighted to see you."},
                {"Miserable","adj","Khon kho","/miz.er.e.bel/","The cold weather made him miserable."},
                {"Optimistic","adj","Lac quan","/op.ti.mis.tik/","Stay optimistic about the future."},
                {"Pessimistic","adj","Bi quan","/pes.i.mis.tik/","He is always pessimistic about everything."},
                {"Sympathy","n","Su cam thong","/sim.pe.thi/","I have a lot of sympathy for her."},
                {"Compassion","n","Long trac an","/kem.pash.en/","We should treat animals with compassion."},
                {"Sincere","adj","Chan thanh","/sin.seer/","Please accept my sincere apologies."},
                {"Stubborn","adj","Buong binh","/stub.ern/","He is too stubborn to admit he is wrong."},
                {"Arrogant","adj","Kieu ngao","/ar.e.gent/","His arrogant attitude annoyed everyone."},
                {"Humble","adj","Khiem ton","/hum.bel/","Despite his wealth, he remains humble."},
                {"Generous","adj","Hao phong","/jen.er.es/","It was generous of you to pay for dinner."},
                {"Selfish","adj","Ich ky","/sel.fish/","Do not be so selfish, share your toys."},
                {"Outgoing","adj","Co mo hoa dong","/owt.go.ing/","She has an outgoing personality."},
                {"Shy","adj","Xau ho rut re","/shy/","The boy was too shy to speak."},
                {"Aggressive","adj","Hung hang","/e.gres.iv/","An aggressive marketing campaign."},
                {"Passive","adj","Thu dong","/pas.iv/","He is very passive in meetings."},
                {"Tolerant","adj","Khoan dung","/tol.er.ent/","We must be tolerant of different views."},
                {"Hostile","adj","Thu dich","/hos.tyl/","The crowd was very hostile."},
                {"Moody","adj","Tinh khi that thuong","/moo.di/","Teenagers can be very moody."}
            };
            insertWords(s, w4, 4);
        }
    }

    private static void insertWords(Statement s, String[][] words, int lessonId) throws Exception {
        for (String[] w : words) {
            s.execute("INSERT OR IGNORE INTO Vocabularies (word,type,meaning,pronunciation,example,lesson_id) VALUES ('" +
                w[0] + "','" + w[1] + "','" + w[2] + "','" + w[3] + "','" + w[4] + "'," + lessonId + ")");
        }
    }
}
