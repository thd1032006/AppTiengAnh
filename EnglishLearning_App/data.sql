-- ====================================================================
-- 1. XÓA SẠCH CÁC BẢNG CŨ ĐỂ TRÁNH XUNG ĐỘT
-- ====================================================================
DROP TABLE IF EXISTS UserProgress;
DROP TABLE IF EXISTS Vocabularies;
DROP TABLE IF EXISTS Lessons;
DROP TABLE IF EXISTS Users;

-- ====================================================================
-- 2. TẠO LẠI HỆ THỐNG 4 BẢNG CHUẨN ĐẦY ĐỦ
-- ====================================================================

-- Bảng 1: Quản lý người dùng
CREATE TABLE Users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    username TEXT NOT NULL UNIQUE,
    password TEXT NOT NULL
);

-- Bảng 2: Quản lý chủ đề bài học
CREATE TABLE Lessons (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    lesson_name TEXT NOT NULL,
    description TEXT
);

-- Bảng 3: Quản lý kho từ vựng (Liên kết với bảng Lessons)
CREATE TABLE Vocabularies (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    word TEXT NOT NULL,
    type TEXT,
    meaning TEXT NOT NULL,
    pronunciation TEXT,
    example TEXT,
    lesson_id INTEGER,
    FOREIGN KEY (lesson_id) REFERENCES Lessons(id)
);

-- Bảng 4: Quản lý tiến độ học/Từ vựng yêu thích (Liên kết Users và Vocabularies)
CREATE TABLE UserProgress (
    user_id INTEGER,
    vocabulary_id INTEGER,
    is_mastered INTEGER DEFAULT 0, -- 1: Đã thuộc, 0: Chưa thuộc
    is_favorite INTEGER DEFAULT 0, -- 1: Yêu thích, 0: Bình thường
    PRIMARY KEY (user_id, vocabulary_id),
    FOREIGN KEY (user_id) REFERENCES Users(id),
    FOREIGN KEY (vocabulary_id) REFERENCES Vocabularies(id)
);

-- ====================================================================
-- 3. NẠP DỮ LIỆU BAN ĐẦU (4 CHỦ ĐỀ & 120 TỪ VỰNG)
-- ====================================================================

-- Nạp dữ liệu vào bảng Lessons
INSERT INTO Lessons (id, lesson_name, description) VALUES 
(1, 'Công sở & Sự nghiệp', 'Business & Career'),
(2, 'Du lịch & Sinh tồn', 'Travel & Survival'),
(3, 'Công nghệ & Tương lai', 'Tech & Future'),
(4, 'Cảm xúc & Giao tiếp xã hội', 'Emotions & Socializing');

-- Nạp 120 từ vựng vào bảng Vocabularies
INSERT INTO Vocabularies (word, type, meaning, pronunciation, example, lesson_id) VALUES 
-- Chủ đề 1: Công sở & Sự nghiệp (lesson_id = 1)
('Colleague', 'n', 'Đồng nghiệp', '/ˈkɒl.iːɡ/', 'I get along well with my colleagues.', 1),
('Negotiate', 'v', 'Đàm phán', '/nəˈɡəʊ.ʃi.eɪt/', 'We need to negotiate a better deal.', 1),
('Promotion', 'n', 'Sự thăng chức', '/prəˈməʊ.ʃən/', 'She got a promotion last month.', 1),
('Productivity', 'n', 'Năng suất', '/ˌprɒd.ʌkˈtɪv.ə.ti/', 'Coffee increases my productivity.', 1),
('Contract', 'n', 'Hợp đồng', '/ˈkɒn.trækt/', 'Please sign the contract here.', 1),
('Interview', 'n', 'Cuộc phỏng vấn', '/ˈɪn.tə.vjuː/', 'I have a job interview tomorrow.', 1),
('Resume', 'n', 'Sơ yếu lý lịch', '/ˈrez.jə.meɪ/', 'Send your resume to the HR manager.', 1),
('Salary', 'n', 'Tiền lương', '/ˈsæl.ər.i/', 'He earns a high salary.', 1),
('Revenue', 'n', 'Doanh thu', '/ˈrev.ən.juː/', 'Company revenue grew by 20%.', 1),
('Budget', 'n', 'Ngân sách', '/ˈbʌdʒ.ɪt/', 'We are on a tight budget.', 1),
('Strategy', 'n', 'Chiến lược', '/ˈstræt.ə.dʒi/', 'We need a new marketing strategy.', 1),
('Management', 'n', 'Sự quản lý', '/ˈmæn.ɪdʒ.mənt/', 'Good management is key to success.', 1),
('Leadership', 'n', 'Khả năng lãnh đạo', '/ˈliː.də.ʃɪp/', 'He has strong leadership skills.', 1),
('Meeting', 'n', 'Cuộc họp', '/ˈmiː.tɪŋ/', 'The meeting starts at 9 AM.', 1),
('Presentation', 'n', 'Bài thuyết trình', '/ˌprez.ənˈteɪ.ʃən/', 'Her presentation was excellent.', 1),
('Report', 'n', 'Báo cáo', '/rɪˈpɔːt/', 'Please submit the report by Friday.', 1),
('Investment', 'n', 'Sự đầu tư', '/ɪnˈvest.mənt/', 'Buying land is a good investment.', 1),
('Client', 'n', 'Khách hàng', '/ˈklaɪ.ənt/', 'We have a meeting with a client.', 1),
('Customer', 'n', 'Người mua hàng', '/ˈkʌs.tə.mər/', 'Customer satisfaction is our priority.', 1),
('Partnership', 'n', 'Sự hợp tác', '/ˈpɑːt.nə.ʃɪp/', 'We entered into a partnership.', 1),
('Startup', 'n', 'Công ty khởi nghiệp', '/ˈstɑːt.ʌp/', 'He works for a tech startup.', 1),
('Entrepreneur', 'n', 'Doanh nhân', '/ˌɒn.trə.prəˈnɜːr/', 'She is a successful entrepreneur.', 1),
('Profit', 'n', 'Lợi nhuận', '/ˈprɒf.ɪt/', 'The company made a huge profit.', 1),
('Loss', 'n', 'Thua lỗ', '/lɒs/', 'The business suffered a great loss.', 1),
('Market', 'n', 'Thị trường', '/ˈmɑː.kɪt/', 'The housing market is booming.', 1),
('Competitor', 'n', 'Đối thủ cạnh tranh', '/kəmˈpet.ɪ.tər/', 'We must beat our competitors.', 1),
('Feedback', 'n', 'Phản hồi', '/ˈfiːd.bæk/', 'We value your honest feedback.', 1),
('Signature', 'n', 'Chữ ký', '/ˈsɪɡ.nə.tʃər/', 'Put your signature at the bottom.', 1),
('Agreement', 'n', 'Thỏa thuận', '/əˈɡriː.mənt/', 'They reached an agreement.', 1),
('Resign', 'v', 'Từ chức/Nghỉ việc', '/rɪˈzaɪn/', 'He decided to resign from his job.', 1),

-- Chủ đề 2: Du lịch & Sinh tồn (lesson_id = 2)
('Accommodation', 'n', 'Chỗ ở', '/əˌkɒm.əˈdeɪ.ʃən/', 'We booked our accommodation online.', 2),
('Departure', 'n', 'Sự khởi hành', '/dɪˈpɑː.tʃər/', 'Our departure is scheduled for 8 AM.', 2),
('Itinerary', 'n', 'Lịch trình', '/aɪˈtɪn.ər.ər.i/', 'Let me check our travel itinerary.', 2),
('Luggage', 'n', 'Hành lý', '/ˈlʌɡ.ɪdʒ/', 'Don''t leave your luggage unattended.', 2),
('Destination', 'n', 'Điểm đến', '/ˌdes.tɪˈneɪ.ʃən/', 'Paris is a popular tourist destination.', 2),
('Passport', 'n', 'Hộ chiếu', '/ˈpɑːs.pɔːt/', 'You need a valid passport to travel.', 2),
('Visa', 'n', 'Thị thực', '/ˈviː.zə/', 'I applied for a tourist visa.', 2),
('Flight', 'n', 'Chuyến bay', '/flaɪt/', 'My flight was delayed by two hours.', 2),
('Boarding', 'n', 'Lên máy bay/tàu', '/ˈbɔː.dɪŋ/', 'Boarding starts at gate 5.', 2),
('Delay', 'v/n', 'Trì hoãn', '/dɪˈleɪ/', 'The heavy rain caused a delay.', 2),
('Souvenir', 'n', 'Đồ lưu niệm', '/ˌsuː.vənˈɪər/', 'I bought a souvenir for my mom.', 2),
('Sightseeing', 'n', 'Ngắm cảnh', '/ˈsaɪtˌsiː.ɪŋ/', 'We went sightseeing in Rome.', 2),
('Currency', 'n', 'Tiền tệ', '/ˈkʌr.ən.si/', 'The local currency is the Yen.', 2),
('Exchange', 'v/n', 'Trao đổi/Đổi tiền', '/ɪksˈtʃeɪndʒ/', 'Where can I exchange my money?', 2),
('Reservation', 'n', 'Sự đặt chỗ', '/ˌrez.əˈveɪ.ʃən/', 'I have a reservation under the name John.', 2),
('Reception', 'n', 'Quầy lễ tân', '/rɪˈsep.ʃən/', 'Leave your keys at the reception.', 2),
('Backpack', 'n', 'Ba lô', '/ˈbæk.pæk/', 'He traveled across Europe with a backpack.', 2),
('Guide', 'n', 'Hướng dẫn viên', '/ɡaɪd/', 'Our tour guide was very friendly.', 2),
('Cruise', 'n', 'Chuyến du ngoạn biển', '/kruːz/', 'They went on a luxury cruise.', 2),
('Journey', 'n', 'Hành trình', '/ˈdʒɜː.ni/', 'Life is a journey.', 2),
('Expedition', 'n', 'Cuộc thám hiểm', '/ˌek.spəˈdɪʃ.ən/', 'An expedition to the North Pole.', 2),
('Altitude', 'n', 'Độ cao', '/ˈæl.tɪ.tʃuːd/', 'We are flying at a high altitude.', 2),
('Landmark', 'n', 'Địa danh nổi bật', '/ˈlænd.mɑːk/', 'The Eiffel Tower is a famous landmark.', 2),
('Monument', 'n', 'Đài kỷ niệm', '/ˈmɒn.jə.mənt/', 'A monument was built in his honor.', 2),
('Scenery', 'n', 'Phong cảnh', '/ˈsiː.nər.i/', 'The mountain scenery is breathtaking.', 2),
('Map', 'n', 'Bản đồ', '/mæp/', 'We looked at the map to find our way.', 2),
('Compass', 'n', 'La bàn', '/ˈkʌm.pəs/', 'A compass points to the North.', 2),
('Customs', 'n', 'Hải quan', '/ˈkʌs.təmz/', 'We had to pass through customs.', 2),
('Emergency', 'n', 'Trường hợp khẩn cấp', '/ɪˈmɜː.dʒən.si/', 'Call 911 in case of an emergency.', 2),
('Transport', 'n', 'Giao thông/Vận tải', '/ˈtræn.spɔːt/', 'Public transport here is very cheap.', 2),

-- Chủ đề 3: Công nghệ & Tương lai (lesson_id = 3)
('Algorithm', 'n', 'Thuật toán', '/ˈæl.ɡə.rɪ.ðəm/', 'YouTube uses a complex algorithm.', 3),
('Innovation', 'n', 'Sự đổi mới', '/ˌɪn.əˈveɪ.ʃən/', 'Apple is known for its innovation.', 3),
('Database', 'n', 'Cơ sở dữ liệu', '/ˈdeɪ.tə.beɪs/', 'The user info is stored in a database.', 3),
('Virtual', 'adj', 'Ảo', '/ˈvɜː.tʃu.əl/', 'Virtual reality is the future of gaming.', 3),
('Cybersecurity', 'n', 'An ninh mạng', '/ˌsaɪ.bə.sɪˈkjʊə.rə.ti/', 'Cybersecurity is crucial for banks.', 3),
('Artificial', 'adj', 'Nhân tạo', '/ˌɑː.tɪˈfɪʃ.əl/', 'Artificial intelligence is growing fast.', 3),
('Hardware', 'n', 'Phần cứng', '/ˈhɑːd.weər/', 'Upgrading hardware improves performance.', 3),
('Software', 'n', 'Phần mềm', '/ˈsɒft.weər/', 'I need to install new software.', 3),
('Network', 'n', 'Mạng lưới', '/ˈnet.wɜːk/', 'The computer network is down.', 3),
('Server', 'n', 'Máy chủ', '/ˈsɜː.vər/', 'The game server is currently offline.', 3),
('Cloud', 'n', 'Đám mây (lưu trữ)', '/klaʊd/', 'All my photos are saved in the cloud.', 3),
('Application', 'n', 'Ứng dụng', '/ˌæp.lɪˈkeɪ.ʃən/', 'This application is very useful.', 3),
('Interface', 'n', 'Giao diện', '/ˈɪn.tə.feɪs/', 'The user interface is clean and simple.', 3),
('Function', 'n', 'Chức năng/Hàm', '/ˈfʌŋk.ʃən/', 'What is the function of this button?', 3),
('Variable', 'n', 'Biến số', '/ˈveə.ri.ə.bəl/', 'Declare a variable before using it.', 3),
('Framework', 'n', 'Khuôn khổ/Cấu trúc', '/ˈfreɪm.wɜːk/', 'Spring is a popular Java framework.', 3),
('Deploy', 'v', 'Triển khai', '/dɪˈplɔɪ/', 'We will deploy the app tomorrow.', 3),
('Debug', 'v', 'Gỡ lỗi', '/ˌdiːˈbʌɡ/', 'It took me hours to debug this code.', 3),
('Optimize', 'v', 'Tối ưu hóa', '/ˈɒp.tɪ.maɪz/', 'We need to optimize the database.', 3),
('Encryption', 'n', 'Sự mã hóa', '/ɪnˈkrɪp.ʃən/', 'End-to-end encryption keeps messages safe.', 3),
('Automate', 'v', 'Tự động hóa', '/ˈɔː.tə.meɪt/', 'We should automate repetitive tasks.', 3),
('Robotics', 'n', 'Ngành chế tạo robot', '/rəʊˈbɒt.ɪks/', 'He is studying robotics in college.', 3),
('Blockchain', 'n', 'Chuỗi khối', '/ˈblɒk.tʃeɪn/', 'Bitcoin is based on blockchain technology.', 3),
('Gadget', 'n', 'Tiện ích/Thiết bị', '/ˈɡædʒ.ɪt/', 'He loves buying new tech gadgets.', 3),
('Device', 'n', 'Thiết bị', '/dɪˈvaɪs/', 'Please turn off your mobile devices.', 3),
('Upgrade', 'v/n', 'Nâng cấp', '/ʌpˈɡreɪd/', 'I need to upgrade my laptop RAM.', 3),
('Install', 'v', 'Cài đặt', '/ɪnˈstɔːl/', 'How do I install this program?', 3),
('Backup', 'n/v', 'Sao lưu', '/ˈbæk.ʌp/', 'Always make a backup of your files.', 3),
('Protocol', 'n', 'Giao thức', '/ˈprəʊ.tə.kɒl/', 'HTTP is a internet protocol.', 3),
('Code', 'n/v', 'Mã/Viết mã', '/kəʊd/', 'He writes Java code every day.', 3),

-- Chủ đề 4: Cảm xúc & Giao tiếp xã hội (lesson_id = 4)
('Extrovert', 'n', 'Người hướng ngoại', '/ˈek.strə.vɜːt/', 'As an extrovert, he loves going to parties.', 4),
('Empathetic', 'adj', 'Đồng cảm', '/ˌem.pəˈθet.ɪk/', 'She is a very empathetic listener.', 4),
('Overwhelmed', 'adj', 'Choáng ngợp', '/ˌəʊ.vəˈwelmd/', 'I feel overwhelmed by all this work.', 4),
('Anxious', 'adj', 'Lo âu', '/ˈæŋk.ʃəs/', 'He was anxious about the test results.', 4),
('Enthusiastic', 'adj', 'Nhiệt huyết', '/ɪnˌθjuː.ziˈæs.tɪk/', 'They are enthusiastic about the new project.', 4),
('Introvert', 'n', 'Người hướng nội', '/ˈɪn.trə.vɜːt/', 'An introvert prefers quiet spaces.', 4),
('Confident', 'adj', 'Tự tin', '/ˈkɒn.fɪ.dənt/', 'She is confident in her abilities.', 4),
('Insecure', 'adj', 'Thiếu tự tin/Bất an', '/ˌɪn.sɪˈkjʊər/', 'Teenagers often feel insecure.', 4),
('Jealous', 'adj', 'Ghen tị', '/ˈdʒel.əs/', 'Don''t be jealous of his success.', 4),
('Grateful', 'adj', 'Biết ơn', '/ˈɡreɪt.fəl/', 'I am grateful for your help.', 4),
('Frustrated', 'adj', 'Bực bội/Nản lòng', '/frʌsˈtreɪ.tɪd/', 'I get frustrated when my code doesn''t work.', 4),
('Delighted', 'adj', 'Vui mừng', '/dɪˈlaɪ.tɪd/', 'We are delighted to see you.', 4),
('Miserable', 'adj', 'Khốn khổ', '/ˈmɪz.ər.ə.bəl/', 'The cold weather made him miserable.', 4),
('Optimistic', 'adj', 'Lạc quan', '/ˌɒp.tɪˈmɪs.tɪk/', 'Stay optimistic about the future.', 4),
('Pessimistic', 'adj', 'Bi quan', '/ˌpes.ɪˈmɪs.tɪk/', 'He is always pessimistic about everything.', 4),
('Sympathy', 'n', 'Sự cảm thông', '/ˈsɪm.pə.fi/', 'I have a lot of sympathy for her.', 4),
('Compassion', 'n', 'Lòng trắc ẩn', '/kəmˈpæʃ.ən/', 'We should treat animals with compassion.', 4),
('Sincere', 'adj', 'Chân thành', '/sɪnˈsɪər/', 'Please accept my sincere apologies.', 4),
('Stubborn', 'adj', 'Bướng bỉnh', '/ˈstʌb.ən/', 'He is too stubborn to admit he is wrong.', 4),
('Arrogant', 'adj', 'Kiêu ngạo', '/ˈær.ə.ɡənt/', 'His arrogant attitude annoyed everyone.', 4),
('Humble', 'adj', 'Khiêm tốn', '/ˈhʌm.bəl/', 'Despite his wealth, he remains humble.', 4),
('Generous', 'adj', 'Hào phóng', '/ˈdʒen.ər.əs/', 'It was generous of you to pay for dinner.', 4),
('Selfish', 'adj', 'Ích kỷ', '/ˈsel.fɪʃ/', 'Don''t be so selfish, share your toys.', 4),
('Outgoing', 'adj', 'Cởi mở/Hòa đồng', '/ˌaʊtˈɡəʊ.ɪŋ/', 'She has an outgoing personality.', 4),
('Shy', 'adj', 'Xấu hổ/Rụt rè', '/ʃaɪ/', 'The boy was too shy to speak.', 4),
('Aggressive', 'adj', 'Hung hăng/Xông xáo', '/əˈɡres.ɪv/', 'An aggressive marketing campaign.', 4),
('Passive', 'adj', 'Thụ động', '/ˈpæs.ɪv/', 'He is very passive in meetings.', 4),
('Tolerant', 'adj', 'Khoan dung/Chịu đựng', '/ˈtɒl.ər.ənt/', 'We must be tolerant of different views.', 4),
('Hostile', 'adj', 'Thù địch', '/ˈhɒs.taɪl/', 'The crowd was very hostile.', 4),
('Moody', 'adj', 'Tính khí thất thường', '/ˈmuː.di/', 'Teenagers can be very moody.', 4);