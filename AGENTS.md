# 工作规则：技能优先

本项目内置了工程技能库 `skills/skills/`，已通过 `.opencode/skills/` 链接注册为 opencode 技能，可按名称加载。

## 硬性要求

任何代码更改之前，必须先加载并遵循相关的技能，再动手改代码。

1. **对齐需求**（需求不清晰或有歧义时）：
   - `grill-with-docs`：带文档打磨的访谈，同时维护领域词汇（`CONTEXT.md`）与 ADR。
   - `grill-me`：纯访谈，把计划或设计的所有分支问清。
2. **规划与设计**（动手编码前）：
   - `to-spec` / `to-tickets`：把已讨论的共识转成 spec 或任务清单。
   - `codebase-design`：涉及模块、接口、深度或 seam 时先按其纪律设计。
3. **实现纪律**：
   - `tdd`：写功能或修 bug 采用红-绿-重构循环，一次一个垂直切片，只在约定的 seam 写测试。
   - `diagnosing-bugs`：复杂 bug 或性能回归按固定诊断循环执行。
   - `research`：需要查证事实/来源时先研究再动手。
4. **完成前评审**：
   - `code-review`：按标准与原始 spec 双轴评审本次改动，再收尾。
5. **不确定用哪个技能**：先加载 `ask-matt` 做路由。

## 加载方式

- 优先用技能工具按名称加载（如 `tdd`、`code-review`、`grill-with-docs`）。
- 若技能加载失败或内容缺失，直接读取 `skills/skills/<bucket>/<name>/SKILL.md` 原文并严格遵循。

## 与其他规则的关系

- `RULES.md` 是本项目爬虫的领域与解析规则，与技能同等重要，两者都要遵守。
- 编辑技能一律改源仓库 `skills/skills/`；`.opencode/skills/` 只是本地链接。增删改名后重新链接保持同步。
- 首次使用工程技能时可运行 `setup-matt-pocock-skills` 配置问题追踪器与文档目录（可选）。
- 单行式琐碎修改（改错字、常量、格式）可直接完成，但仍须遵守 `RULES.md` 与仓库约定。
