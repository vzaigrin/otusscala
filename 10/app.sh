#!/usr/bin/sh

# Авторизуемся
token=$(curl -H "Content-Type: application/json" -X POST -d '{"username":"admin","password":"admin"}' http://127.0.0.1:8080/api/login | jq '.token' | sed -e 's/"//g')

# Создаём роли
roleReader=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d '{"name":"Reader"}' http://127.0.0.1:8080/api/role | jq '.id')
roleManager=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d '{"name":"Manager"}' http://127.0.0.1:8080/api/role | jq '.id')

# Список всех ролей
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X GET http://127.0.0.1:8080/api/role | jq

# Создаём пользователей
# password = `echo -n $1 | md5sum | awk '{print $1}'`
userReaderData='{"userName":"reader","password":"1de9b0a30075ae8c303eb420c103c320","firstName":"Иван","lastName":"Петров","age":50,"roles":['$roleReader']}'
reader=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d "$userReaderData" http://127.0.0.1:8080/api/user | jq '.id')
userManagerData='{"userName":"manager","password":"1d0258c2440a8d19e716292b231e3190","firstName":"Петр","lastName":"Семенов","age":20,"roles":['$roleManager']}'
manager=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d "$userManagerData" http://127.0.0.1:8080/api/user | jq '.id')

# Список всех пользователей
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X GET http://127.0.0.1:8080/api/user | jq

# Создаём авторов
tolstoy=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d '{"firstName":"Лев","lastName":"Толстой"}' http://127.0.0.1:8080/api/author | jq '.id')
dostoevsky=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d '{"firstName":"Фёдор","lastName":"Достоевский"}' http://127.0.0.1:8080/api/author | jq '.id')
gogol=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d '{"firstName":"Николай","lastName":"Гоголь"}' http://127.0.0.1:8080/api/author | jq '.id')
pushkin=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d '{"firstName":"Александр","lastName":"Пушкин"}' http://127.0.0.1:8080/api/author | jq '.id')
lermontov=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d '{"firstName":"Михаил","lastName":"Лермонтов"}' http://127.0.0.1:8080/api/author | jq '.id')

# Список всех авторов
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X GET http://127.0.0.1:8080/api/author | jq

# Создаём книги
echo '{"title":"Великие романы","authors":['"$tolstoy"','"$dostoevsky"'],"published":1914,"pages":1300}' > tmp.json
book1=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book  | jq '.id')
echo '{"title":"Великие поэмы","authors":['"$pushkin"','"$lermontov"'],"published":1850,"pages":600}' > tmp.json
book2=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book  | jq '.id')
echo '{"title":"Война и мир","authors":['"$tolstoy"'],"published":1869,"pages":1274}' > tmp.json
book3=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book  | jq '.id')
echo '{"title":"Анна Каренина","authors":['"$tolstoy"'],"published":1877,"pages":864}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Воскресение","authors":['"$tolstoy"'],"published":1899, "pages":640}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Набег","authors":['"$tolstoy"'],"published":1853,"pages":196}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"После бала","authors":['"$tolstoy"'],"published":1911,"pages":9}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Униженные и оскорблённые","authors":['"$dostoevsky"'],"published":1861,"pages":512}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Преступление и наказание","authors":['"$dostoevsky"'],"published":1866,"pages":672}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Идиот","authors":['"$dostoevsky"'],"published":1869,"pages":640}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Бесы","authors":['"$dostoevsky"'],"published":1872,"pages":768}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Братья Карамазовы","authors":['"$dostoevsky"'],"published":1880,"pages":992}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Вечера на хуторе близ Диканьки","authors":['"$gogol"'],"published":1832,"pages":320}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Вий","authors":['"$gogol"'],"published":1835,"pages":48}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Мёртвые души","authors":['"$gogol"'],"published":1842,"pages":352}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Нос","authors":['"$gogol"'],"published":1836,"pages":26}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Шинель","authors":['"$gogol"'],"published":1843,"pages":36}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Руслан и Людмила","authors":['"$pushkin"'],"published":1820,"pages":146}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Полтава","authors":['"$pushkin"'],"published":1829,"pages":88}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Медный всадник","authors":['"$pushkin"'],"published":1837,"pages":55}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Евгений Онегин","authors":['"$pushkin"'],"published":1832,"pages":448}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Сказка о рыбаке и рыбке","authors":['"$pushkin"'],"published":1835,"pages":3}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Бородино","authors":['"$lermontov"'],"published":1837,"pages":64}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Герой нашего времени","authors":['"$lermontov"'],"published":1840,"pages":224}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Демон","authors":['"$lermontov"'],"published":1842,"pages":75}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Парус","authors":['"$lermontov"'],"published":1841,"pages":1}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Беглец","authors":['"$lermontov"'],"published":1846,"pages":18}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/book

# Список всех книг
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X GET http://127.0.0.1:8080/api/book | jq

# Получаем id пользователя admin
admin=$(curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X GET http://127.0.0.1:8080/api/user/find?username=admin | jq '.[0].id')

# Добавляем записи
echo '{"user":'"$admin"',"book":'"$book1"',"getDT":"2020-09-01T13:28:05","returnDT":"2020-10-01T12:38:05"}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/record
echo '{"user":'"$reader"',"book":'"$book2"',"getDT":"2020-09-02T14:38:05","returnDT":"2020-10-02T13:48:05"}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/record
echo '{"user":'"$manager"',"book":'"$book3"',"getDT":"2020-09-03T15:48:05","returnDT":"2020-10-03T14:58:05"}' > tmp.json
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X POST -d @tmp.json http://127.0.0.1:8080/api/record

# Список всех записей
curl -H "Content-Type: application/json" -H "Authorization: Bearer $token" -X GET http://127.0.0.1:8080/api/record | jq

rm -f tmp.json
