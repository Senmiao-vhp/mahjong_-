# Details

Date : 2025-03-17 22:39:08

Directory d:\\吾日三省吾身\\项目开发\\mahjong_all

Total : 48 files,  3731 codes, 466 comments, 615 blanks, all 4812 lines

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)

## Files
| filename | language | code | comment | blank | total |
| :--- | :--- | ---: | ---: | ---: | ---: |
| [mahjong\_backend/README.md](/mahjong_backend/README.md) | Markdown | 49 | 0 | 20 | 69 |
| [mahjong\_backend/pom.xml](/mahjong_backend/pom.xml) | XML | 100 | 19 | 10 | 129 |
| [mahjong\_backend/src/main/java/com/example/mahjong/MahjongApplication.java](/mahjong_backend/src/main/java/com/example/mahjong/MahjongApplication.java) | Java | 13 | 0 | 3 | 16 |
| [mahjong\_backend/src/main/java/com/example/mahjong/common/JwtAuthInterceptor.java](/mahjong_backend/src/main/java/com/example/mahjong/common/JwtAuthInterceptor.java) | Java | 68 | 12 | 15 | 95 |
| [mahjong\_backend/src/main/java/com/example/mahjong/common/JwtUtil.java](/mahjong_backend/src/main/java/com/example/mahjong/common/JwtUtil.java) | Java | 93 | 19 | 28 | 140 |
| [mahjong\_backend/src/main/java/com/example/mahjong/common/Result.java](/mahjong_backend/src/main/java/com/example/mahjong/common/Result.java) | Java | 21 | 0 | 4 | 25 |
| [mahjong\_backend/src/main/java/com/example/mahjong/config/JwtConfig.java](/mahjong_backend/src/main/java/com/example/mahjong/config/JwtConfig.java) | Java | 21 | 0 | 7 | 28 |
| [mahjong\_backend/src/main/java/com/example/mahjong/config/WebConfig.java](/mahjong_backend/src/main/java/com/example/mahjong/config/WebConfig.java) | Java | 27 | 2 | 9 | 38 |
| [mahjong\_backend/src/main/java/com/example/mahjong/controller/AuthController.java](/mahjong_backend/src/main/java/com/example/mahjong/controller/AuthController.java) | Java | 127 | 28 | 42 | 197 |
| [mahjong\_backend/src/main/java/com/example/mahjong/controller/GameController.java](/mahjong_backend/src/main/java/com/example/mahjong/controller/GameController.java) | Java | 206 | 49 | 72 | 327 |
| [mahjong\_backend/src/main/java/com/example/mahjong/controller/HelloController.java](/mahjong_backend/src/main/java/com/example/mahjong/controller/HelloController.java) | Java | 10 | 0 | 3 | 13 |
| [mahjong\_backend/src/main/java/com/example/mahjong/controller/RoomController.java](/mahjong_backend/src/main/java/com/example/mahjong/controller/RoomController.java) | Java | 225 | 50 | 72 | 347 |
| [mahjong\_backend/src/main/java/com/example/mahjong/entity/Game.java](/mahjong_backend/src/main/java/com/example/mahjong/entity/Game.java) | Java | 23 | 0 | 2 | 25 |
| [mahjong\_backend/src/main/java/com/example/mahjong/entity/PlayerGame.java](/mahjong_backend/src/main/java/com/example/mahjong/entity/PlayerGame.java) | Java | 20 | 0 | 2 | 22 |
| [mahjong\_backend/src/main/java/com/example/mahjong/entity/Room.java](/mahjong_backend/src/main/java/com/example/mahjong/entity/Room.java) | Java | 18 | 0 | 2 | 20 |
| [mahjong\_backend/src/main/java/com/example/mahjong/entity/RoomDTO.java](/mahjong_backend/src/main/java/com/example/mahjong/entity/RoomDTO.java) | Java | 18 | 0 | 2 | 20 |
| [mahjong\_backend/src/main/java/com/example/mahjong/entity/User.java](/mahjong_backend/src/main/java/com/example/mahjong/entity/User.java) | Java | 12 | 0 | 2 | 14 |
| [mahjong\_backend/src/main/java/com/example/mahjong/entity/UserDTO.java](/mahjong_backend/src/main/java/com/example/mahjong/entity/UserDTO.java) | Java | 8 | 0 | 2 | 10 |
| [mahjong\_backend/src/main/java/com/example/mahjong/exception/GlobalExceptionHandler.java](/mahjong_backend/src/main/java/com/example/mahjong/exception/GlobalExceptionHandler.java) | Java | 21 | 7 | 5 | 33 |
| [mahjong\_backend/src/main/java/com/example/mahjong/mapper/GameMapper.java](/mahjong_backend/src/main/java/com/example/mahjong/mapper/GameMapper.java) | Java | 23 | 12 | 7 | 42 |
| [mahjong\_backend/src/main/java/com/example/mahjong/mapper/PlayerGameMapper.java](/mahjong_backend/src/main/java/com/example/mahjong/mapper/PlayerGameMapper.java) | Java | 29 | 18 | 10 | 57 |
| [mahjong\_backend/src/main/java/com/example/mahjong/mapper/RoomMapper.java](/mahjong_backend/src/main/java/com/example/mahjong/mapper/RoomMapper.java) | Java | 30 | 15 | 9 | 54 |
| [mahjong\_backend/src/main/java/com/example/mahjong/mapper/UserMapper.java](/mahjong_backend/src/main/java/com/example/mahjong/mapper/UserMapper.java) | Java | 16 | 12 | 7 | 35 |
| [mahjong\_backend/src/main/java/com/example/mahjong/service/GameService.java](/mahjong_backend/src/main/java/com/example/mahjong/service/GameService.java) | Java | 19 | 36 | 15 | 70 |
| [mahjong\_backend/src/main/java/com/example/mahjong/service/RoomService.java](/mahjong_backend/src/main/java/com/example/mahjong/service/RoomService.java) | Java | 14 | 21 | 10 | 45 |
| [mahjong\_backend/src/main/java/com/example/mahjong/service/UserService.java](/mahjong_backend/src/main/java/com/example/mahjong/service/UserService.java) | Java | 13 | 24 | 10 | 47 |
| [mahjong\_backend/src/main/java/com/example/mahjong/service/impl/GameServiceImpl.java](/mahjong_backend/src/main/java/com/example/mahjong/service/impl/GameServiceImpl.java) | Java | 365 | 78 | 98 | 541 |
| [mahjong\_backend/src/main/java/com/example/mahjong/service/impl/RoomServiceImpl.java](/mahjong_backend/src/main/java/com/example/mahjong/service/impl/RoomServiceImpl.java) | Java | 142 | 26 | 44 | 212 |
| [mahjong\_backend/src/main/java/com/example/mahjong/service/impl/UserServiceImpl.java](/mahjong_backend/src/main/java/com/example/mahjong/service/impl/UserServiceImpl.java) | Java | 97 | 17 | 30 | 144 |
| [mahjong\_backend/src/main/resources/application.yml](/mahjong_backend/src/main/resources/application.yml) | YAML | 22 | 0 | 3 | 25 |
| [mahjong\_backend/src/main/resources/init.sql](/mahjong_backend/src/main/resources/init.sql) | MS SQL | 204 | 1 | 17 | 222 |
| [mahjong\_backend/src/main/resources/test\_data.sql](/mahjong_backend/src/main/resources/test_data.sql) | MS SQL | 93 | 15 | 14 | 122 |
| [mahjong\_backend/src/test/java/com/example/hello/HelloApplicationTest.java](/mahjong_backend/src/test/java/com/example/hello/HelloApplicationTest.java) | Java | 3 | 0 | 2 | 5 |
| [mahjong\_frontend/README.md](/mahjong_frontend/README.md) | Markdown | 3 | 0 | 3 | 6 |
| [mahjong\_frontend/index.html](/mahjong_frontend/index.html) | HTML | 13 | 0 | 1 | 14 |
| [mahjong\_frontend/package-lock.json](/mahjong_frontend/package-lock.json) | JSON | 1,361 | 0 | 1 | 1,362 |
| [mahjong\_frontend/package.json](/mahjong_frontend/package.json) | JSON | 21 | 0 | 1 | 22 |
| [mahjong\_frontend/public/vite.svg](/mahjong_frontend/public/vite.svg) | XML | 1 | 0 | 0 | 1 |
| [mahjong\_frontend/src/App.vue](/mahjong_frontend/src/App.vue) | vue | 28 | 0 | 3 | 31 |
| [mahjong\_frontend/src/assets/vue.svg](/mahjong_frontend/src/assets/vue.svg) | XML | 1 | 0 | 0 | 1 |
| [mahjong\_frontend/src/components/HelloWorld.vue](/mahjong_frontend/src/components/HelloWorld.vue) | vue | 35 | 0 | 7 | 42 |
| [mahjong\_frontend/src/main.ts](/mahjong_frontend/src/main.ts) | TypeScript | 4 | 0 | 2 | 6 |
| [mahjong\_frontend/src/style.css](/mahjong_frontend/src/style.css) | CSS | 70 | 0 | 10 | 80 |
| [mahjong\_frontend/src/vite-env.d.ts](/mahjong_frontend/src/vite-env.d.ts) | TypeScript | 0 | 1 | 1 | 2 |
| [mahjong\_frontend/tsconfig.app.json](/mahjong_frontend/tsconfig.app.json) | JSON | 12 | 1 | 2 | 15 |
| [mahjong\_frontend/tsconfig.json](/mahjong_frontend/tsconfig.json) | JSON with Comments | 7 | 0 | 1 | 8 |
| [mahjong\_frontend/tsconfig.node.json](/mahjong_frontend/tsconfig.node.json) | JSON | 20 | 2 | 3 | 25 |
| [mahjong\_frontend/vite.config.ts](/mahjong_frontend/vite.config.ts) | TypeScript | 5 | 1 | 2 | 8 |

[Summary](results.md) / Details / [Diff Summary](diff.md) / [Diff Details](diff-details.md)