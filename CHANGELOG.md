# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [v0.7.0] - 2025-12-05
### :sparkles: New Features
- [`09225bd`](https://github.com/QuadStingray/quartz-manager/commit/09225bd9332acbfcfa9b178eb00d8c911cbb20d5) - REST API Structure *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`a88531a`](https://github.com/QuadStingray/quartz-manager/commit/a88531ab9778972782d1fa670b77abb32b3b5dcc) - implementing job trigger *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`52ede9a`](https://github.com/QuadStingray/quartz-manager/commit/52ede9a48b260a17e4cb436964d28c741cbe7251) - add authentication service and configuration support *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`2e1adff`](https://github.com/QuadStingray/quartz-manager/commit/2e1adff649ec9fba9609aa5cc5c2af235672c838) - implement job history *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`c340351`](https://github.com/QuadStingray/quartz-manager/commit/c34035160bf8d384981aad7719c61b51ea032ca8) - add scheduler routes for starting, standing by, and shutting down the Quartz Scheduler *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8ad3d6c`](https://github.com/QuadStingray/quartz-manager/commit/8ad3d6c3ec7436557a93cb6887d6f720ebda1342) - add scheduler routes for starting, standing by, and shutting down the Quartz Scheduler *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`4ffde1c`](https://github.com/QuadStingray/quartz-manager/commit/4ffde1cec2b2232bae645467c1697500a9db78f6) - update job execution routes to accept optional job data map *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`f5ec659`](https://github.com/QuadStingray/quartz-manager/commit/f5ec659701e9abb91a289cc55ff254e2dc0489fb) - add OpenAPI and Swagger configuration for API documentation *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`18f07d1`](https://github.com/QuadStingray/quartz-manager/commit/18f07d1611139a63dccab54b3f3a2f5d8229ca13) - initialize Nuxt.js application with basic configuration and dependencies *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b7f0624`](https://github.com/QuadStingray/quartz-manager/commit/b7f062418a5c837a4e5dbfae5c3b2144048f2ec4) - add initial setup for Nuxt.js project *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`19b033f`](https://github.com/QuadStingray/quartz-manager/commit/19b033f69ac1bf2359e4b05dd12b342208977268) - configure nuxt like https://github.com/sfxcode/nuxt3-primevue-starter *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`d1ac990`](https://github.com/QuadStingray/quartz-manager/commit/d1ac99081da8734796b8bc5982e36f6ccbbfe1b0) - enhance authentication service with token-based and basic-auth mechanisms, and update related routes and models *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8118e4c`](https://github.com/QuadStingray/quartz-manager/commit/8118e4c5167acce4d18968ca1e165db95a27faca) - add TypeScript API client for Quartz Manager including models, runtime, composables, and login page setup *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`9a7437b`](https://github.com/QuadStingray/quartz-manager/commit/9a7437b50d4875974ca86238870388c2bc5261e1) - implement enhanced authentication flow with token management, auth layout, and login page redesign *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`9a0ad69`](https://github.com/QuadStingray/quartz-manager/commit/9a0ad69097abdefada18e2e010eb521601b053ee) - add job execution logging and abstract `executeJob` method in HistoryJob *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`e89aed7`](https://github.com/QuadStingray/quartz-manager/commit/e89aed7615ddbf4be4a9de6debfc7e4bcaac3d32) - add support for one-time trigger registration with scheduling and execution logic *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`56ee488`](https://github.com/QuadStingray/quartz-manager/commit/56ee4888447130f3b15a562f6939ce1281b47d49) - extend `JobSchedulerService` to handle `JobDataMap` and improve trigger time calculations *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`3e040c6`](https://github.com/QuadStingray/quartz-manager/commit/3e040c6dcf0769b6ec87b741e94f7484829294f1) - added auth in ui *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`c2af606`](https://github.com/QuadStingray/quartz-manager/commit/c2af60607c2d404d318b65d33b0cb05f8f815388) - jobs list page *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`0a8b7be`](https://github.com/QuadStingray/quartz-manager/commit/0a8b7beb3764ab718dd3116cc2ea84d5224ec726) - Add endpoint to fetch job history by ID *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`2f428e2`](https://github.com/QuadStingray/quartz-manager/commit/2f428e2bd9cc3f35f537d880b567e9df28aac168) - ui for job history *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b5aa8af`](https://github.com/QuadStingray/quartz-manager/commit/b5aa8af541b71d364b28d41b78a727eff1470672) - Add new endpoint for scheduler information *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b2ad34c`](https://github.com/QuadStingray/quartz-manager/commit/b2ad34c39fde37b7a570ecbbd8d5797876688939) - Add system routes and overview models for scheduler information *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`fa8fca2`](https://github.com/QuadStingray/quartz-manager/commit/fa8fca2242da48ae4e6caba10600577ac736614c) - added dashboard for system informations *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b34482b`](https://github.com/QuadStingray/quartz-manager/commit/b34482bb4b4c736a381bdf1c7847b2adfaaad45c) - add toggle scheduler button and integrate API calls in the dashboard *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`6703a0d`](https://github.com/QuadStingray/quartz-manager/commit/6703a0dabb48e21f15f584f912406fb5dadd4e03) - add UI for creating new job including form validation and API integration *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`cfff6e5`](https://github.com/QuadStingray/quartz-manager/commit/cfff6e580571bfaff3157a85f23373324e2afce4) - add UI for creating one-time triggers including form validation and API integration *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`c2405c2`](https://github.com/QuadStingray/quartz-manager/commit/c2405c2af5a2787f5a926e6f3d76aae6bf748ca0) - add new endpoint to retrieve job information *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`5302c4d`](https://github.com/QuadStingray/quartz-manager/commit/5302c4d435f7496029402202a4dcdf1b9775904f) - add custom CORS configuration to HTTP server *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`d9a86a1`](https://github.com/QuadStingray/quartz-manager/commit/d9a86a1cd3787d39f22849a0988f82d81d073412) - add UI for updating job configurations including form validation and API integration *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`7566c72`](https://github.com/QuadStingray/quartz-manager/commit/7566c72a727389f944731315541c647bab2e25cb) - changes for scala 3 compatible *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :bug: Bug Fixes
- [`843ffa9`](https://github.com/QuadStingray/quartz-manager/commit/843ffa90a4853c840237760643f8c26cde512b62) - new server on every test class

### :recycle: Refactors
- [`a27d524`](https://github.com/QuadStingray/quartz-manager/commit/a27d52495d7bf40201ad91ccc5a03d5b1a28c5d1) - style and project structure *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`d93841e`](https://github.com/QuadStingray/quartz-manager/commit/d93841efc5c5ab0019df275cb8dcd1889b8f7337) - regenerate client and fix test *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`478633e`](https://github.com/QuadStingray/quartz-manager/commit/478633e6afb32d53591189e90f354b53175dcc4f) - streamline config *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8a7572d`](https://github.com/QuadStingray/quartz-manager/commit/8a7572de6d4b68fee3d76a954d6fdbbb30cb5ea4) - internationalization for pages *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`2e7ac6a`](https://github.com/QuadStingray/quartz-manager/commit/2e7ac6ad6cdfe9f33efcb86ec53290c428f4514f) - update UI text elements with internationalization for job-related pages *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :white_check_mark: Tests
- [`20aeef4`](https://github.com/QuadStingray/quartz-manager/commit/20aeef4d3101e790b5255c852a5eadbd74ed4b70) - rename BaseServerSuite to UiServerSuite *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`9407940`](https://github.com/QuadStingray/quartz-manager/commit/94079408a0f9a923606ae270db4896dab138899d) - regenerate client *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`ed597ec`](https://github.com/QuadStingray/quartz-manager/commit/ed597ecb7157c879a183c5c39cd4bdfabc4aa35c) - added some more tests *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :wrench: Chores
- [`e376f53`](https://github.com/QuadStingray/quartz-manager/commit/e376f537086415ca42bb2031015ae2dba1d37f80) - update sbt-scalafmt plugin to version 2.5.5 *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`ff25171`](https://github.com/QuadStingray/quartz-manager/commit/ff2517159cd663fb715656b222bfca50ce19a8f4) - optimize imports and reorganize ApiDocsRoutes package for consistency *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`41e39e0`](https://github.com/QuadStingray/quartz-manager/commit/41e39e0cc811efe54d41ebf5e79ef8dca0c85661) - depdendency updates *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8ebb3dd`](https://github.com/QuadStingray/quartz-manager/commit/8ebb3ddcfce9e2fcede353804adc9614ec19cca5) - Mark public endpoints as private and cleanup imports. Adjust indentation in `reference.conf`. *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`778aa25`](https://github.com/QuadStingray/quartz-manager/commit/778aa256b9595ea15b01911df8e1c87b52bf3677) - **ui**: dependency updates *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`edc4c50`](https://github.com/QuadStingray/quartz-manager/commit/edc4c5075c2bb0fbb46fc323856a180e8153151b) - specify package manager as pnpm 10.24.0 in package.json *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`77234b8`](https://github.com/QuadStingray/quartz-manager/commit/77234b859bf15ae542012c9499ad94402bc96fb1) - update devDependencies to the latest versions in package.json *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`57b7024`](https://github.com/QuadStingray/quartz-manager/commit/57b7024628641e74f831540ef2d2002bdecc44f4) - **api**: chore: 15 dependency updates for quartz-manager-api
- [`1693257`](https://github.com/QuadStingray/quartz-manager/commit/169325778ce269e7552d0e2e85ce3b7076272db7) - **build**: add cross-compilation for Scala 3.7.4


## [v0.6.0] - 2025-12-05
### :sparkles: New Features
- [`09225bd`](https://github.com/QuadStingray/quartz-manager/commit/09225bd9332acbfcfa9b178eb00d8c911cbb20d5) - REST API Structure *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`a88531a`](https://github.com/QuadStingray/quartz-manager/commit/a88531ab9778972782d1fa670b77abb32b3b5dcc) - implementing job trigger *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`52ede9a`](https://github.com/QuadStingray/quartz-manager/commit/52ede9a48b260a17e4cb436964d28c741cbe7251) - add authentication service and configuration support *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`2e1adff`](https://github.com/QuadStingray/quartz-manager/commit/2e1adff649ec9fba9609aa5cc5c2af235672c838) - implement job history *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`c340351`](https://github.com/QuadStingray/quartz-manager/commit/c34035160bf8d384981aad7719c61b51ea032ca8) - add scheduler routes for starting, standing by, and shutting down the Quartz Scheduler *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8ad3d6c`](https://github.com/QuadStingray/quartz-manager/commit/8ad3d6c3ec7436557a93cb6887d6f720ebda1342) - add scheduler routes for starting, standing by, and shutting down the Quartz Scheduler *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`4ffde1c`](https://github.com/QuadStingray/quartz-manager/commit/4ffde1cec2b2232bae645467c1697500a9db78f6) - update job execution routes to accept optional job data map *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`f5ec659`](https://github.com/QuadStingray/quartz-manager/commit/f5ec659701e9abb91a289cc55ff254e2dc0489fb) - add OpenAPI and Swagger configuration for API documentation *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`18f07d1`](https://github.com/QuadStingray/quartz-manager/commit/18f07d1611139a63dccab54b3f3a2f5d8229ca13) - initialize Nuxt.js application with basic configuration and dependencies *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b7f0624`](https://github.com/QuadStingray/quartz-manager/commit/b7f062418a5c837a4e5dbfae5c3b2144048f2ec4) - add initial setup for Nuxt.js project *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`19b033f`](https://github.com/QuadStingray/quartz-manager/commit/19b033f69ac1bf2359e4b05dd12b342208977268) - configure nuxt like https://github.com/sfxcode/nuxt3-primevue-starter *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`d1ac990`](https://github.com/QuadStingray/quartz-manager/commit/d1ac99081da8734796b8bc5982e36f6ccbbfe1b0) - enhance authentication service with token-based and basic-auth mechanisms, and update related routes and models *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8118e4c`](https://github.com/QuadStingray/quartz-manager/commit/8118e4c5167acce4d18968ca1e165db95a27faca) - add TypeScript API client for Quartz Manager including models, runtime, composables, and login page setup *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`9a7437b`](https://github.com/QuadStingray/quartz-manager/commit/9a7437b50d4875974ca86238870388c2bc5261e1) - implement enhanced authentication flow with token management, auth layout, and login page redesign *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`9a0ad69`](https://github.com/QuadStingray/quartz-manager/commit/9a0ad69097abdefada18e2e010eb521601b053ee) - add job execution logging and abstract `executeJob` method in HistoryJob *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`e89aed7`](https://github.com/QuadStingray/quartz-manager/commit/e89aed7615ddbf4be4a9de6debfc7e4bcaac3d32) - add support for one-time trigger registration with scheduling and execution logic *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`56ee488`](https://github.com/QuadStingray/quartz-manager/commit/56ee4888447130f3b15a562f6939ce1281b47d49) - extend `JobSchedulerService` to handle `JobDataMap` and improve trigger time calculations *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`3e040c6`](https://github.com/QuadStingray/quartz-manager/commit/3e040c6dcf0769b6ec87b741e94f7484829294f1) - added auth in ui *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`c2af606`](https://github.com/QuadStingray/quartz-manager/commit/c2af60607c2d404d318b65d33b0cb05f8f815388) - jobs list page *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`0a8b7be`](https://github.com/QuadStingray/quartz-manager/commit/0a8b7beb3764ab718dd3116cc2ea84d5224ec726) - Add endpoint to fetch job history by ID *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`2f428e2`](https://github.com/QuadStingray/quartz-manager/commit/2f428e2bd9cc3f35f537d880b567e9df28aac168) - ui for job history *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b5aa8af`](https://github.com/QuadStingray/quartz-manager/commit/b5aa8af541b71d364b28d41b78a727eff1470672) - Add new endpoint for scheduler information *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b2ad34c`](https://github.com/QuadStingray/quartz-manager/commit/b2ad34c39fde37b7a570ecbbd8d5797876688939) - Add system routes and overview models for scheduler information *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`fa8fca2`](https://github.com/QuadStingray/quartz-manager/commit/fa8fca2242da48ae4e6caba10600577ac736614c) - added dashboard for system informations *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`b34482b`](https://github.com/QuadStingray/quartz-manager/commit/b34482bb4b4c736a381bdf1c7847b2adfaaad45c) - add toggle scheduler button and integrate API calls in the dashboard *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`6703a0d`](https://github.com/QuadStingray/quartz-manager/commit/6703a0dabb48e21f15f584f912406fb5dadd4e03) - add UI for creating new job including form validation and API integration *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`cfff6e5`](https://github.com/QuadStingray/quartz-manager/commit/cfff6e580571bfaff3157a85f23373324e2afce4) - add UI for creating one-time triggers including form validation and API integration *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`c2405c2`](https://github.com/QuadStingray/quartz-manager/commit/c2405c2af5a2787f5a926e6f3d76aae6bf748ca0) - add new endpoint to retrieve job information *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`5302c4d`](https://github.com/QuadStingray/quartz-manager/commit/5302c4d435f7496029402202a4dcdf1b9775904f) - add custom CORS configuration to HTTP server *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`d9a86a1`](https://github.com/QuadStingray/quartz-manager/commit/d9a86a1cd3787d39f22849a0988f82d81d073412) - add UI for updating job configurations including form validation and API integration *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :bug: Bug Fixes
- [`843ffa9`](https://github.com/QuadStingray/quartz-manager/commit/843ffa90a4853c840237760643f8c26cde512b62) - new server on every test class

### :recycle: Refactors
- [`a27d524`](https://github.com/QuadStingray/quartz-manager/commit/a27d52495d7bf40201ad91ccc5a03d5b1a28c5d1) - style and project structure *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`d93841e`](https://github.com/QuadStingray/quartz-manager/commit/d93841efc5c5ab0019df275cb8dcd1889b8f7337) - regenerate client and fix test *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`478633e`](https://github.com/QuadStingray/quartz-manager/commit/478633e6afb32d53591189e90f354b53175dcc4f) - streamline config *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8a7572d`](https://github.com/QuadStingray/quartz-manager/commit/8a7572de6d4b68fee3d76a954d6fdbbb30cb5ea4) - internationalization for pages *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`2e7ac6a`](https://github.com/QuadStingray/quartz-manager/commit/2e7ac6ad6cdfe9f33efcb86ec53290c428f4514f) - update UI text elements with internationalization for job-related pages *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :white_check_mark: Tests
- [`20aeef4`](https://github.com/QuadStingray/quartz-manager/commit/20aeef4d3101e790b5255c852a5eadbd74ed4b70) - rename BaseServerSuite to UiServerSuite *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`9407940`](https://github.com/QuadStingray/quartz-manager/commit/94079408a0f9a923606ae270db4896dab138899d) - regenerate client *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`ed597ec`](https://github.com/QuadStingray/quartz-manager/commit/ed597ecb7157c879a183c5c39cd4bdfabc4aa35c) - added some more tests *(commit by [@QuadStingray](https://github.com/QuadStingray))*

### :wrench: Chores
- [`e376f53`](https://github.com/QuadStingray/quartz-manager/commit/e376f537086415ca42bb2031015ae2dba1d37f80) - update sbt-scalafmt plugin to version 2.5.5 *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`ff25171`](https://github.com/QuadStingray/quartz-manager/commit/ff2517159cd663fb715656b222bfca50ce19a8f4) - optimize imports and reorganize ApiDocsRoutes package for consistency *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`41e39e0`](https://github.com/QuadStingray/quartz-manager/commit/41e39e0cc811efe54d41ebf5e79ef8dca0c85661) - depdendency updates *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`8ebb3dd`](https://github.com/QuadStingray/quartz-manager/commit/8ebb3ddcfce9e2fcede353804adc9614ec19cca5) - Mark public endpoints as private and cleanup imports. Adjust indentation in `reference.conf`. *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`778aa25`](https://github.com/QuadStingray/quartz-manager/commit/778aa256b9595ea15b01911df8e1c87b52bf3677) - **ui**: dependency updates *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`edc4c50`](https://github.com/QuadStingray/quartz-manager/commit/edc4c5075c2bb0fbb46fc323856a180e8153151b) - specify package manager as pnpm 10.24.0 in package.json *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`77234b8`](https://github.com/QuadStingray/quartz-manager/commit/77234b859bf15ae542012c9499ad94402bc96fb1) - update devDependencies to the latest versions in package.json *(commit by [@QuadStingray](https://github.com/QuadStingray))*
- [`57b7024`](https://github.com/QuadStingray/quartz-manager/commit/57b7024628641e74f831540ef2d2002bdecc44f4) - **api**: chore: 15 dependency updates for quartz-manager-api
- [`1693257`](https://github.com/QuadStingray/quartz-manager/commit/169325778ce269e7552d0e2e85ce3b7076272db7) - **build**: add cross-compilation for Scala 3.7.4

[v0.6.0]: https://github.com/QuadStingray/quartz-manager/compare/v0.0.1...v0.6.0
[v0.7.0]: https://github.com/QuadStingray/quartz-manager/compare/v0.0.1...v0.7.0
