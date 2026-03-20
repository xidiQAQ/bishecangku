-- 插入心理测试数据

-- 1. 插入SAS焦虑自评量表
INSERT INTO psychological_test (id, name, description, test_type, total_questions, time_limit, cover_image, status, created_at, updated_at, deleted)
VALUES (1, 'SAS焦虑自评量表', '焦虑自评量表（Self-Rating Anxiety Scale，SAS）是一种分析病人主观症状的相当简便的临床工具，适用于具有焦虑症状的成年人。本测验共20题，请根据您最近一周的实际感受进行选择。', 'SAS', 20, 10, NULL, 1, NOW(), NOW(), 0);

-- 2. 插入SDS抑郁自评量表
INSERT INTO psychological_test (id, name, description, test_type, total_questions, time_limit, cover_image, status, created_at, updated_at, deleted)
VALUES (2, 'SDS抑郁自评量表', '抑郁自评量表（Self-Rating Depression Scale，SDS）是一种用于评定抑郁状态的简便工具，适用于具有抑郁症状的成年人。本测验共20题，请根据您最近一周的实际感受进行选择。', 'SDS', 20, 10, NULL, 1, NOW(), NOW(), 0);

-- 3. 插入SAS测试题目
INSERT INTO test_question (test_id, question_no, question_text, options, question_type, created_at, updated_at) VALUES
(1, 1, '我觉得比平常容易紧张或着急', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 2, '我无缘无故地感到害怕', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 3, '我容易心里烦乱或觉得惊恐', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 4, '我觉得我可能将要发疯', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 5, '我觉得一切都很好，也不会发生什么不幸', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(1, 6, '我手脚发抖打颤', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 7, '我因为头痛、颈痛和背痛而苦恼', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 8, '我感觉容易衰弱和疲乏', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 9, '我觉得心平气和，并且容易安静坐着', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(1, 10, '我觉得心跳得很快', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 11, '我因为一阵阵头晕而苦恼', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 12, '我有晕倒发作，或觉得要晕倒似的', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 13, '我吸气呼气都感到很容易', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(1, 14, '我的手脚麻木和刺痛', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 15, '我因为胃痛和消化不良而苦恼', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 16, '我常常要小便', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 17, '我的手常常是干燥温暖的', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(1, 18, '我脸红发热', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(1, 19, '我容易入睡并且一夜睡得很好', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(1, 20, '我做恶梦', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW());

-- 4. 插入SDS测试题目
INSERT INTO test_question (test_id, question_no, question_text, options, question_type, created_at, updated_at) VALUES
(2, 1, '我感到情绪沮丧，郁闷', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 2, '我感到早晨心情最好', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(2, 3, '我要哭或想哭', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 4, '我夜间睡眠不好', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 5, '我吃饭像平时一样多', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(2, 6, '我的性功能正常', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(2, 7, '我感到体重减轻', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 8, '我为便秘烦恼', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 9, '我的心跳比平时快', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 10, '我无故感到疲劳', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 11, '我的头脑像往常一样清楚', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(2, 12, '我做事情像平时一样不感到困难', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(2, 13, '我坐卧不安，难以保持平静', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 14, '我对未来感到有希望', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(2, 15, '我比平时更容易激怒', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 16, '我觉得决定什么事很容易', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(2, 17, '我感到自己是有用的和不可缺少的人', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(2, 18, '我的生活很有意义', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW()),
(2, 19, '假若我死了别人会过得更好', '[{"key":"A","value":"没有或很少时间","score":1},{"key":"B","value":"小部分时间","score":2},{"key":"C","value":"相当多时间","score":3},{"key":"D","value":"绝大部分或全部时间","score":4}]', 1, NOW(), NOW()),
(2, 20, '我仍旧喜爱自己平时喜爱的东西', '[{"key":"A","value":"没有或很少时间","score":4},{"key":"B","value":"小部分时间","score":3},{"key":"C","value":"相当多时间","score":2},{"key":"D","value":"绝大部分或全部时间","score":1}]', 1, NOW(), NOW());
