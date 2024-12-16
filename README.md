### Домашнее задание 1

Студент - Семенов Владислав (vliusemenov@edu.hse.ru).

В рамках домашнего задания был разработан генератор случайных чисел с логнормальным распределением. Интерфейс приложения:

<img src="https://github.com/user-attachments/assets/afc3ad41-092a-433a-a587-899cb0393873" width="206" height="446"/>

Добавил вывод сообщения, если хотя бы одно из полей пустое или распределение генерирует слишком большие числа.

Также чуть переписал инструментальный тест: теперь массив сгенерированных чисел (точнее, их логарифмов) проверяется на нормальность тестом Колмогорова-Смирнова (смотрю значение p-value и сравниваю с порогом).

Формат вывода чисел в задании указан не был, к тому же текст берется для математических расчетов (а тут вроде как нужна точность), поэтому полученное случайное число преобразую в строку с помощью простого toString.

Workflow в GitHub Actions отработал, однако:
- для прохождения этапа Detekt пришлось подправить инструментальный тест (в нескольких местах Detekt ругался на неиспользуемые переменные и функции);
- пришлось вносить правки в android.yaml для для отработки инструментального теста.
