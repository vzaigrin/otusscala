#!/usr/bin/sh

# Создаём роли
role=$(curl -H "Content-Type: application/json" -X POST -d '{"name":"Reader"}' http://127.0.0.1:8080/api/role)
roleReader=$(echo "$role" | sed -e 's/ //g')
role=$(curl -H "Content-Type: application/json" -X POST -d '{"name":"Manager"}' http://127.0.0.1:8080/api/role)
roleManager=$(echo "$role" | sed -e 's/ //g')
role=$(curl -H "Content-Type: application/json" -X POST -d '{"name":"Admin"}' http://127.0.0.1:8080/api/role)
roleAdmin=$(echo "$role" | sed -e 's/ //g')

# Список всех ролей
curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/role

# Get by ID, Delete, Update
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/role/?id=431f5b81-da57-4b40-a2a6-3c0e59cba562
# curl -H "Content-Type: application/json" -X DELETE http://127.0.0.1:8080/api/role/431f5b81-da57-4b40-a2a6-3c0e59cba562
# curl -H "Content-Type: application/json" -X PUT -d '{"id" : "62a9a23d-13aa-4b70-a714-1faa634955bd", "name":"Boss"}' http://127.0.0.1:8080/api/role

# Создаём пользователей
# password = `echo -n $1 | md5sum | awk '{print $1}'`
userReaderData='{"userName":"reader","password":"1de9b0a30075ae8c303eb420c103c320","firstName":"Иван","lastName":"Петров","age":50,"roles":['$roleReader']}'
user=$(curl -H "Content-Type: application/json" -X POST -d "$userReaderData"  http://127.0.0.1:8080/api/user)
reader=$(echo "$user" | sed -e 's/ //g')
userManagerData='{"userName":"manager","password":"1d0258c2440a8d19e716292b231e3190","firstName":"Петр","lastName":"Семенов","age":20,"roles":['$roleManager']}'
user=$(curl -H "Content-Type: application/json" -X POST -d "$userManagerData" http://127.0.0.1:8080/api/user)
manager=$(echo "$user" | sed -e 's/ //g')
userAdminData='{"userName":"admin","password":"21232f297a57a5a743894a0e4a801fc3","firstName":"Семен","lastName":"Иванов","age":30,"roles":['$roleAdmin']}'
user=$(curl -H "Content-Type: application/json" -X POST -d "$userAdminData" http://127.0.0.1:8080/api/user)
admin=$(echo "$user" | sed -e 's/ //g')

# Список всех пользователей
curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/user

# Get, Find, Delete, Update
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/user/?id=1fcec16d-bf7c-4213-aea8-240a07f3100b
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/user/?username=manager
# curl -H "Content-Type: application/json" -X DELETE http://127.0.0.1:8080/api/user/38b89a85-7702-48c2-b6c1-8df513f4a027

# Создаём авторов
author=$(curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Лев","lastName":"Толстой"}' http://127.0.0.1:8080/api/author)
tolstoy=$(echo "$author" | sed -e 's/ //g')
author=$(curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Фёдор","lastName":"Достоевский"}' http://127.0.0.1:8080/api/author)
dostoevsky=$(echo "$author" | sed -e 's/ //g')
author=$(curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Николай","lastName":"Гоголь"}' http://127.0.0.1:8080/api/author)
gogol=$(echo "$author" | sed -e 's/ //g')
author=$(curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Александр","lastName":"Пушкин"}' http://127.0.0.1:8080/api/author)
pushkin=$(echo "$author" | sed -e 's/ //g')
author=$(curl -H "Content-Type: application/json" -X POST -d '{"firstName":"Михаил","lastName":"Лермонтов"}' http://127.0.0.1:8080/api/author)
lermontov=$(echo "$author" | sed -e 's/ //g')

# Список всех авторов
curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/author

# Get by ID, Delete, Update
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/author/?id=4dc1e8b0-d59e-4f6b-95dd-da8001e97f8d
# curl -H "Content-Type: application/json" -X DELETE http://127.0.0.1:8080/api/author/5c03137d-1b36-4f89-974c-c2731bb4f60b
# curl -H "Content-Type: application/json" -X PUT -d '{"id" : "4dc1e8b0-d59e-4f6b-95dd-da8001e97f8d", "firstName":"Michael", "lastName":"Lermontoff"}' http://127.0.0.1:8080/api/author
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/author/?lastname=Лермонтов
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/author/?lastname=%D0%9B%D0%B5%D1%80%D0%BC%D0%BE%D0%BD%D1%82%D0%BE%D0%B2

# Создаём книги
echo '{"title":"Великие романы","authors":['"$tolstoy"','"$dostoevsky"'],"published":1914,"pages":1300}' > tmp.json
book=$(curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book)
book1=$(echo "$book" | sed -e 's/ //g')
echo '{"title":"Великие поэмы","authors":['"$pushkin"','"$lermontov"'],"published":1850,"pages":600}' > tmp.json
book=$(curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book)
book2=$(echo "$book" | sed -e 's/ //g')
echo '{"title":"Война и мир","authors":['"$tolstoy"'],"published":1869,"pages":1274}' > tmp.json
book=$(curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book)
book3=$(echo "$book" | sed -e 's/ //g')
echo '{"title":"Анна Каренина","authors":['"$tolstoy"'],"published":1877,"pages":864}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Воскресение","authors":['"$tolstoy"'],"published":1899, "pages":640}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Набег","authors":['"$tolstoy"'],"published":1853,"pages":196}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"После бала","authors":['"$tolstoy"'],"published":1911,"pages":9}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Униженные и оскорблённые","authors":['"$dostoevsky"'],"published":1861,"pages":512}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Преступление и наказание","authors":['"$dostoevsky"'],"published":1866,"pages":672}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Идиот","authors":['"$dostoevsky"'],"published":1869,"pages":640}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Бесы","authors":['"$dostoevsky"'],"published":1872,"pages":768}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Братья Карамазовы","authors":['"$dostoevsky"'],"published":1880,"pages":992}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Вечера на хуторе близ Диканьки","authors":['"$gogol"'],"published":1832,"pages":320}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Вий","authors":['"$gogol"'],"published":1835,"pages":48}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Мёртвые души","authors":['"$gogol"'],"published":1842,"pages":352}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Нос","authors":['"$gogol"'],"published":1836,"pages":26}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Шинель","authors":['"$gogol"'],"published":1843,"pages":36}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Руслан и Людмила","authors":['"$pushkin"'],"published":1820,"pages":146}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Полтава","authors":['"$pushkin"'],"published":1829,"pages":88}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Медный всадник","authors":['"$pushkin"'],"published":1837,"pages":55}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Евгений Онегин","authors":['"$pushkin"'],"published":1832,"pages":448}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Сказка о рыбаке и рыбке","authors":['"$pushkin"'],"published":1835,"pages":3}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Бородино","authors":['"$lermontov"'],"published":1837,"pages":64}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Герой нашего времени","authors":['"$lermontov"'],"published":1840,"pages":224}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Демон","authors":['"$lermontov"'],"published":1842,"pages":75}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Парус","authors":['"$lermontov"'],"published":1841,"pages":1}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book
echo '{"title":"Беглец","authors":['"$lermontov"'],"published":1846,"pages":18}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/book

# Список всех книг
curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/book

# Get, Find
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/book/?id=b6db3dd0-44a6-478c-9a19-7752d3d18691
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/book/?year=1832
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/book/?pages=3
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/book/?authorId=80d3cb5a-a5b3-4108-9f11-5328cb5fa6bb
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/book/?title=%D0%98%D0%B4%D0%B8%D0%BE%D1%82
# [ {
#   "id" : "5a028d40-76ee-47d0-b060-cc3742cb347d",
#   "title" : "Идиот",
#   "authors" : [ {
#     "id" : "8265b17b-9ff2-40eb-8d77-6b027ced1a62",
#     "firstName" : "Фёдор",
#     "lastName" : "Достоевский"
#   } ],
#   "published" : 1869,
#   "pages" : 640
# } ]

# Добавляем записи
echo '{"user":'"$admin"',"book":'"$book1"',"getDT":"2020-09-01 13:28:05","returnDT":"2020-10-01T12:38:05Z"}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/record
echo '{"user":'"$reader"',"book":'"$book2"',"getDT":"2020-09-02 14:38:05","returnDT":"2020-10-02T13:48:05Z"}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/record
echo '{"user":'"$manager"',"book":'"$book3"',"getDT":"2020-09-03 15:48:05","returnDT":"2020-10-03T14:58:05Z"}' > tmp.json
curl -H "Content-Type: application/json" -X POST -d @tmp.json http://127.0.0.1:8080/api/record

# Список всех записей
curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/record

# Get, Find, Delete
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/record/?id=dd803492-572b-41d3-a550-5aab72724e70
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/record/?userId=ea3468bb-eb8d-4feb-b4b4-b37885beb432
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/record/?bookId=a389e3ac-b994-4122-9682-c3ad3544fdd2
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/record/?getDT=20200903T154805
# curl -H "Content-Type: application/json" -X GET http://127.0.0.1:8080/api/record/?returnDT=20201001T123805
# curl -H "Content-Type: application/json" -X DELETE http://127.0.0.1:8080/api/record/dd803492-572b-41d3-a550-5aab72724e70

rm -f tmp.json
