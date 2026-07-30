# Claude Code Configuration - Ruflo v3.5

> **Ruflo v3.6** (2026-04-29) — Stable release with agent federation and comms-first coordination.
> 6,000+ commits, 314 MCP tools, 16 agent roles + custom types, 19 AgentDB controllers, 21 native plugins.
> Packages: `@claude-flow/cli@3.6.10`, `claude-flow@3.6.10`, `ruflo@3.6.10`

## Behavioral Rules (Always Enforced)

- Do what has been asked; nothing more, nothing less
- NEVER create files unless they're absolutely necessary for achieving your goal
- ALWAYS prefer editing an existing file to creating a new one
- NEVER proactively create documentation files (*.md) or README files unless explicitly requested
- NEVER save working files, text/mds, or tests to the root folder
- Never continuously check status after spawning a swarm — wait for results
- ALWAYS read a file before editing it
- NEVER commit secrets, credentials, or .env files

## Git Commit Conventions

- НЕ добавлять строку `Co-Authored-By: Claude <noreply@anthropic.com>` (и любой похожий
  trailer, приписывающий авторство модели) в сообщения коммитов — ни в этом, ни в любом
  другом проекте. Коммиты оформляются от имени пользователя, без упоминания Claude/AI
  как соавтора.
- Формулировка commit message — по обычным правилам проекта (краткое summary + при
  необходимости тело с деталями), без добавления служебных AI-related trailer'ов.

## File Organization

- NEVER save to root folder — use the directories below
- Use `/src` for source code files
- Use `/tests` for test files
- Use `/docs` for documentation and markdown files
- Use `/config` for configuration files
- Use `/scripts` for utility scripts
- Use `/examples` for example code

## Project Architecture

- Follow Domain-Driven Design with bounded contexts
- Keep files under 500 lines
- Use typed interfaces for all public APIs
- Prefer TDD London School (mock-first) for new code
- Use event sourcing for state changes
- Ensure input validation at system boundaries

## Concurrency: 1 MESSAGE = ALL RELATED OPERATIONS

- All operations MUST be concurrent/parallel in a single message
- Use Claude Code's Task tool for spawning agents, not just MCP

**Mandatory patterns:**
- ALWAYS batch ALL todos in ONE TodoWrite call (5-10+ minimum)
- ALWAYS spawn ALL agents in ONE message with full instructions via Task tool
- ALWAYS batch ALL file reads/writes/edits in ONE message
- ALWAYS batch ALL terminal operations in ONE Bash message
- ALWAYS batch ALL memory store/retrieve operations in ONE message

---

## Swarm Orchestration

- MUST initialize the swarm using MCP tools when starting complex tasks
- MUST spawn concurrent agents using Claude Code's Task tool
- Never use MCP tools alone for execution — Task tool agents do the actual work

### MCP + Task Tool in SAME Message

- MUST call MCP tools AND Task tool in ONE message for complex work
- Always call MCP first, then IMMEDIATELY call Task tool to spawn agents

### 3-Tier Model Routing (ADR-026, ADR-143)

| Tier | Handler | Latency | Cost | Use Cases |
|------|---------|---------|------|-----------|
| **1** | Deterministic codemod | ~1ms | $0 | Structural transforms with **no LLM**: `var-to-const`, `remove-console`, `add-logging` |
| **2** | Haiku | ~500ms | $0.0002 | Simple tasks, low complexity (<30%) |
| **3** | Sonnet/Opus | 2-5s | $0.003-0.015 | Complex reasoning, architecture, security (>30%) |

- Always check for `[CODEMOD_AVAILABLE]` or `[TASK_MODEL_RECOMMENDATION]` before spawning agents
- When you see `[CODEMOD_AVAILABLE]`, call the `hooks_codemod` MCP tool (intent + file) — it applies the transform deterministically via the TypeScript compiler at $0, no LLM. Deterministic intents only: `var-to-const`, `remove-console`, `add-logging`
- `add-types`, `add-error-handling`, `async-await` need judgement and route to a model (Tier 2/3) — they are **not** $0 codemods (see ADR-143)
- Agent Booster (`agent-booster`) is a fast-apply merge engine for arbitrary LLM-produced edit snippets, not an intent-transform engine — it is **not** the Tier-1 path

## Swarm Configuration & Anti-Drift

### Anti-Drift Coding Swarm (PREFERRED DEFAULT)

- ALWAYS use hierarchical topology for coding swarms
- Keep maxAgents at 6-8 for tight coordination
- Use specialized strategy for clear role boundaries
- Use `raft` consensus for hive-mind (leader maintains authoritative state)
- Run frequent checkpoints via `post-task` hooks
- Keep shared memory namespace for all agents
- Keep task cycles short with verification gates

```javascript
mcp__ruv-swarm__swarm_init({
  topology: "hierarchical",
  maxAgents: 8,
  strategy: "specialized"
})
```

## Swarm Protocols & Routing

### Auto-Start Swarm Protocol

When the user requests a complex task (multi-file changes, feature implementation, refactoring), **immediately execute this pattern in a SINGLE message:**

```javascript
// STEP 1: Initialize swarm coordination via MCP
mcp__ruv-swarm__swarm_init({
  topology: "hierarchical",
  maxAgents: 8,
  strategy: "specialized"
})

// STEP 2: Spawn NAMED agents concurrently — all in ONE message
// Each agent knows WHO to message next in the pipeline
Task({
  prompt: "Research requirements and codebase. SendMessage findings to 'architect' when done.",
  subagent_type: "researcher", name: "researcher", run_in_background: true
})
Task({
  prompt: "Wait for research from 'researcher'. Design implementation. SendMessage design to 'coder'.",
  subagent_type: "system-architect", name: "architect", run_in_background: true
})
Task({
  prompt: "Wait for design from 'architect'. Implement the solution. SendMessage code paths to 'tester'.",
  subagent_type: "coder", name: "coder", run_in_background: true
})
Task({
  prompt: "Wait for implementation from 'coder'. Write tests. SendMessage results to 'reviewer'.",
  subagent_type: "tester", name: "tester", run_in_background: true
})
Task({
  prompt: "Wait for test results from 'tester'. Review code quality and security. Report findings.",
  subagent_type: "reviewer", name: "reviewer", run_in_background: true
})

// STEP 3: Kick off the pipeline
SendMessage({ to: "researcher", summary: "Start research", message: "[task description and context]" })

// STEP 4: Batch todos
TodoWrite({ todos: [
  {content: "Research and analyze requirements", status: "in_progress", activeForm: "Researching"},
  {content: "Design architecture", status: "pending", activeForm: "Designing"},
  {content: "Implement solution", status: "pending", activeForm: "Implementing"},
  {content: "Write tests", status: "pending", activeForm: "Testing"},
  {content: "Review and finalize", status: "pending", activeForm: "Reviewing"}
]})

// Pipeline flow via SendMessage:
// researcher ──→ architect ──→ coder ──→ tester ──→ reviewer
```

### Agent Routing (Anti-Drift)

| Code | Task | Agents |
|------|------|--------|
| 1 | Bug Fix | coordinator, researcher, coder, tester |
| 3 | Feature | coordinator, architect, coder, tester, reviewer |
| 5 | Refactor | coordinator, architect, coder, reviewer |
| 7 | Performance | coordinator, perf-engineer, coder |
| 9 | Security | coordinator, security-architect, auditor |
| 11 | Memory | coordinator, memory-specialist, perf-engineer |
| 13 | Docs | researcher, api-docs |

**Codes 1-11: hierarchical/specialized (anti-drift). Code 13: mesh/balanced**

### Task Complexity Detection

**AUTO-INVOKE SWARM when task involves:**
- Multiple files (3+)
- New feature implementation
- Refactoring across modules
- API changes with tests
- Security-related changes
- Performance optimization
- Database schema changes

**SKIP SWARM for:**
- Single file edits
- Simple bug fixes (1-2 lines)
- Documentation updates
- Configuration changes
- Quick questions/exploration

## Project Configuration

This project is configured with Claude Flow V3 (Anti-Drift Defaults):
- **Topology**: hierarchical (prevents drift via central coordination)
- **Max Agents**: 8 (smaller team = less drift)
- **Strategy**: specialized (clear roles, no overlap)
- **Consensus**: raft (leader maintains authoritative state)
- **Memory Backend**: hybrid (SQLite + AgentDB)
- **HNSW Indexing**: Enabled (measured ~1.9x at N=20k, ~3.2x–4.7x at N=5k vs brute force; ANN wins above the crossover)
- **Neural Learning**: Enabled (SONA)

## V3 CLI Commands (Quick Reference)

```bash
# Initialize project
npx claude-flow@v3alpha init --wizard

# Start daemon with background workers
npx claude-flow@v3alpha daemon start

# Spawn an agent
npx claude-flow@v3alpha agent spawn -t coder --name my-coder

# Initialize swarm
npx claude-flow@v3alpha swarm init --v3-mode

# Search memory (HNSW-indexed)
npx claude-flow@v3alpha memory search -q "authentication patterns"

# System diagnostics
npx claude-flow@v3alpha doctor --fix

# Security scan
npx claude-flow@v3alpha security scan --depth full

# Performance benchmark
npx claude-flow@v3alpha performance benchmark --suite all
```

## Headless Background Instances (claude -p)

Use `claude -p` (print/pipe mode) to spawn headless Claude instances for parallel background work. These run non-interactively and return results to stdout.

### Basic Usage

```bash
# Single headless task
claude -p "Analyze the authentication module for security issues"

# With model selection
claude -p --model haiku "Format this config file"
claude -p --model opus "Design the database schema for user management"

# With output format
claude -p --output-format json "List all TODO comments in src/"
claude -p --output-format stream-json "Refactor the error handling in api.ts"

# With budget limits
claude -p --max-budget-usd 0.50 "Run comprehensive security audit"

# With specific tools allowed
claude -p --allowedTools "Read,Grep,Glob" "Find all files that import the auth module"

# Skip permissions (sandboxed environments only)
claude -p --dangerously-skip-permissions "Fix all lint errors in src/"
```


### Key Flags

| Flag | Purpose |
|------|---------|
| `-p, --print` | Non-interactive mode, print and exit |
| `--model <model>` | Select model (haiku, sonnet, opus) |
| `--output-format <fmt>` | Output: text, json, stream-json |
| `--max-budget-usd <amt>` | Spending cap per invocation |
| `--allowedTools <tools>` | Restrict available tools |
| `--append-system-prompt` | Add custom instructions |
| `--resume <id>` | Continue a previous session |
| `--fork-session` | Branch from resumed session |
| `--fallback-model <model>` | Auto-fallback if primary overloaded |
| `--permission-mode <mode>` | acceptEdits, bypassPermissions, plan, etc. |
| `--mcp-config <json>` | Load MCP servers from JSON |

## Available Agents (60+ Types)

### Core Development
`coder`, `reviewer`, `tester`, `planner`, `researcher`

### V3 Specialized Agents
`security-architect`, `security-auditor`, `memory-specialist`, `performance-engineer`

### @claude-flow/security Module
CVE remediation, input validation, path security:
- `InputValidator` — Zod-based validation at boundaries
- `PathValidator` — Path traversal prevention
- `SafeExecutor` — Command injection protection
- `PasswordHasher` — bcrypt hashing
- `TokenGenerator` — Secure token generation

### Swarm Coordination
`hierarchical-coordinator`, `mesh-coordinator`, `adaptive-coordinator`, `collective-intelligence-coordinator`, `swarm-memory-manager`

### Consensus & Distributed
`byzantine-coordinator`, `raft-manager`, `gossip-coordinator`, `consensus-builder`, `crdt-synchronizer`, `quorum-manager`, `security-manager`

### Performance & Optimization
`perf-analyzer`, `performance-benchmarker`, `task-orchestrator`, `memory-coordinator`, `smart-agent`

### GitHub & Repository
`github-modes`, `pr-manager`, `code-review-swarm`, `issue-tracker`, `release-manager`, `workflow-automation`, `project-board-sync`, `repo-architect`, `multi-repo-swarm`

### SPARC Methodology
`sparc-coord`, `sparc-coder`, `specification`, `pseudocode`, `architecture`, `refinement`

### Specialized Development
`backend-dev`, `mobile-dev`, `ml-developer`, `cicd-engineer`, `api-docs`, `system-architect`, `code-analyzer`, `base-template-generator`

### Testing & Validation
`tdd-london-swarm`, `production-validator`


## Agent Teams & Comms System (SendMessage)

Named agents can coordinate directly via `SendMessage` instead of polling shared memory — useful for fan-out/fan-in or supervisor/worker patterns. Use Task tool with `run_in_background: true` and give each agent explicit instructions on who to message next.

### Rules

1. **Always name agents** — use `name: "role-name"` so they're addressable
2. **Comms over memory** — use SendMessage for real-time coordination, memory for persistence
3. **Pipeline prompts** — tell each agent WHO to message next and WHAT to send
4. **Spawn all at once** — all Task calls in ONE message with `run_in_background: true`
5. **Don't poll** — agents message back when done; wait for task completion notifications
6. **Graceful shutdown** — send `{ type: "shutdown_request" }` before TeamDelete
7. **Lead synthesizes** — when agents complete, review ALL results before responding to user


### Essential Hook Commands

```bash
# Core hooks
npx claude-flow@v3alpha hooks pre-task --description "[task]"
npx claude-flow@v3alpha hooks post-task --task-id "[id]" --success true
npx claude-flow@v3alpha hooks post-edit --file "[file]" --train-patterns

# Session management
npx claude-flow@v3alpha hooks session-start --session-id "[id]"
npx claude-flow@v3alpha hooks session-end --export-metrics true
npx claude-flow@v3alpha hooks session-restore --session-id "[id]"

# Intelligence routing
npx claude-flow@v3alpha hooks route --task "[task]"
npx claude-flow@v3alpha hooks explain --topic "[topic]"

# Neural learning
npx claude-flow@v3alpha hooks pretrain --model-type moe --epochs 10
npx claude-flow@v3alpha hooks build-agents --agent-types coder,tester

# Background workers
npx claude-flow@v3alpha hooks worker list
npx claude-flow@v3alpha hooks worker dispatch --trigger audit
npx claude-flow@v3alpha hooks worker status
```


## Hive-Mind Consensus

### Topologies
- `hierarchical` — Queen controls workers directly
- `mesh` — Fully connected peer network
- `hierarchical-mesh` — Hybrid (recommended)
- `adaptive` — Dynamic based on load

### Consensus Strategies
- `byzantine` — BFT (tolerates f < n/3 faulty)
- `raft` — Leader-based (tolerates f < n/2)
- `gossip` — Epidemic for eventual consistency
- `crdt` — Conflict-free replicated data types
- `quorum` — Configurable quorum-based


## Environment Variables

```bash
# Configuration
CLAUDE_FLOW_CONFIG=./claude-flow.config.json
CLAUDE_FLOW_LOG_LEVEL=info

# Provider API Keys
ANTHROPIC_API_KEY=sk-ant-...
OPENAI_API_KEY=sk-...
GOOGLE_API_KEY=...

# MCP Server
CLAUDE_FLOW_MCP_PORT=3000
CLAUDE_FLOW_MCP_HOST=localhost
CLAUDE_FLOW_MCP_TRANSPORT=stdio

# Memory
CLAUDE_FLOW_MEMORY_BACKEND=hybrid
CLAUDE_FLOW_MEMORY_PATH=./data/memory
```


## Claude Code vs MCP Tools

### Claude Code Handles ALL EXECUTION:
- **Task tool**: Spawn and run agents concurrently
- File operations (Read, Write, Edit, MultiEdit, Glob, Grep)
- Code generation and programming
- Bash commands and system operations
- TodoWrite and task management
- Git operations

### MCP Tools ONLY COORDINATE:
- Swarm initialization (topology setup)
- Agent type definitions
- Task orchestration
- Memory management
- Neural features
- Performance tracking

- Keep MCP for coordination strategy only — use Claude Code's Task tool for real execution

## Claude Code ↔ AgentDB Memory Bridge

Claude Code's auto-memory (`~/.claude/projects/*/memory/*.md`) is bridged to AgentDB with ONNX vector embeddings for semantic search.

### MCP Tools

| Tool | Description |
|------|-------------|
| `memory_import_claude` | Import Claude Code memories into AgentDB with 384-dim ONNX embeddings. Use `allProjects: true` to import from ALL projects. |
| `memory_bridge_status` | Show bridge health — Claude files, AgentDB entries, SONA state, connection status |
| `memory_search_unified` | Semantic search across ALL namespaces (claude-memories, auto-memory, patterns, tasks, feedback) |

### Auto-Import on Session Start

The `SessionStart` hook automatically imports current project's memories into AgentDB. For manual import of all projects:

```bash
# Via MCP tool (from Claude Code)
memory_import_claude({ allProjects: true })

# Via helper hook (from terminal)
node .claude/helpers/auto-memory-hook.mjs import-all
```

### Unified Search

Search across both Claude Code memories and AgentDB entries:

```bash
# Via MCP tool
memory_search_unified({ query: "authentication security", limit: 5 })

# Results include source attribution: claude-code, auto-memory, or agentdb
```

### Intelligence Pipeline

| Component | Status | Details |
|-----------|--------|---------|
| ONNX Embeddings | Active | all-MiniLM-L6-v2, 384 dimensions |
| SONA Learning | Active | Pattern matching + trajectory recording |
| ReasoningBank | Active | Pattern storage with file persistence |
| AgentDB sql.js | Active | SQLite with vector_indexes table |


## Multi-Stage Pipeline Execution Defaults (ring/hierarchical, любой проект)

Применяется к ЛЮБОМУ Ring Pipeline или похожему последовательному пайплайну ролей
(Architect→Planner→Coder→Validator→Reviewer→Tester и т.п.), в любом проекте —
не только Android. Действует ВСЕГДА, без необходимости повторять в промпте:

- Все Task-вызовы внутри пайплайна выполняются СИНХРОННО (без `run_in_background: true`),
  если пользователь явно не попросил фоновый режим.
- НЕ отправлять промежуточные idle/teammate-уведомления в чат — показывать только
  финальный результат каждой роли.
- Ключи `memory_store` для каждой роли ВСЕГДА уникальны: `<role>-handoff-<task-slug>`,
  где `task-slug` — краткий машинно-читаемый идентификатор задачи (из git-ветки
  `feature/<task-slug>` или из формулировки задачи). Никогда не использовать голое
  имя роли (`architect-handoff`) как постоянный ключ — это перезаписывает предыдущие
  результаты и не позволяет отличить старый прогон от нового.
- Перед началом работы каждой роли — проверить/переключиться на нужную git-ветку
  (`git branch --show-current`); не создавать новую ветку, если для этой задачи
  уже существует, и не работать поверх грязной рабочей директории без предупреждения.
- Если система всё равно присылает повторяющиеся teammate/idle-события — не реагировать
  на них повторно, считать за один и тот же ивент.

## Mandatory Ring Pipeline Enforcement (ВСЕГДА, для любой задачи с кодом)

Модель бесплатная (opencode/*-free через локальный прокси) — расход токенов НЕ является
ограничением. Поэтому для ЛЮБОЙ задачи, где создаётся или изменяется код —
новая фича, рефакторинг, написание с нуля, исправление бага — ВСЕГДА оркестрировать
через полный Ring Pipeline (swarm_init topology "ring" + Task tool на каждую роль),
а НЕ делать прямое редактирование одним агентом в обход пайплайна.

- Стандартная последовательность ролей: Architect → Planner → Coder → Validator →
  Reviewer → (Tester/Optimizer, если применимо).
- Не экономить на количестве шагов и не пропускать роли ради скорости/цены —
  предпочитать точность результата, а не экономию токенов.
- Единственное допустимое исключение: тривиальные однострочные правки без
  архитектурных последствий (опечатка, пробел, удаление ЗАВЕДОМО неиспользуемого
  кода, подтверждённого grep/поиском использования по всему проекту) — в этом
  случае можно редактировать напрямую. В любой спорной ситуации — по умолчанию
  запускать полный пайплайн, а не решать самостоятельно, что "достаточно простое".
- Названия ролей/системные промпты для конкретного стека (Android/Kotlin, Node.js,
  Python и т.д.) берутся из проектного `CLAUDE.md`; если проектный файл не
  специализирует роли под этот стек — использовать роли в общей формулировке
  (Architect определяет scope изменений, Planner переводит в конкретный план,
  Coder пишет минимальные патчи, Validator проверяет корректность, Reviewer
  делает финальный обзор).

## Support

- Documentation: https://github.com/ruvnet/claude-flow
- Issues: https://github.com/ruvnet/claude-flow/issues

---

Remember: **Claude Flow coordinates, Claude Code creates!**
