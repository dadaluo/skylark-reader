<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# IntelliJ Platform Plugin Template Changelog

## [Unreleased]

### Changed

- Upgrade Gradle Wrapper to `8.10.2`
- Update `platformVersion` to `2023.3.8`
- Dependencies - upgrade `org.jetbrains.intellij.platform` to `2.1.0`
- Dependencies - upgrade `org.jetbrains.qodana` to `2024.2.3`
- Dependencies (GitHub Actions) - upgrade `gradle/actions/setup-gradle` to `v4`
- Add back the `org.gradle.toolchains.foojay-resolver-convention` Gradle settings plugin

### Fixed

- Fixed _Run Plugin_ run configuration logs location

### Removed

- Removed _Run Qodana_ and _Run UI for UI Tests_ run configurations

## [0.2.0] - 2020-07-02

### Added

- JetBrains Plugin badges and TODO list for the end users
- `ktlint` integration

### Changed

- `pluginUntilBuild` set to the correct format: `201.*`
- Bump detekt dependency to `1.10.0`

### Fixed

- GitHub Actions — Template Cleanup, fixed adding files to git
- Update Template plugin name on cleanup
- Set `buildUponDefaultConfig = true` in detekt configuration

## [0.1.0] - 2020-06-26

### Added

- `settings.gradle.kts` for the [performance purposes](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#always_define_a_settings_file)
- `#REMOVE-ON-CLEANUP#` token to mark content to be removed with **Template Cleanup** workflow

### Changed

- README proofreading
- GitHub Actions — Update IDE versions for the Plugin Verifier
- Update platformVersion to `2020.1.2`

## [0.0.2] - 2020-06-22

### Added

- [Gradle Changelog Plugin](https://github.com/JetBrains/gradle-changelog-plugin) integration

### Changed

- Bump Detekt version
- Change pluginSinceBuild to 193

## [0.0.1]

### Added

- 新建项目
- 通过[intellij-platform-plugin-template](https://github.com/JetBrains/intellij-platform-plugin-template)初始化项目
- 实现了通过console面板阅读text电子书功能
- 实现了epub电子书阅读功能
- 实现了书架管理功能
- 实现了通过status bar widget阅读text电子书功能
- 添加了快捷键翻页、清除阅读面板内容
