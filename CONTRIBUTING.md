# Contributing to Sensei Backend
---

## 📌 Table of Contents

- [Project Setup](#project-setup)
- [How to Contribute](#how-to-contribute)
- [Coding Guidelines](#coding-guidelines)
- [Git Guidelines](#git-guidelines)
- [Reporting Issues](#reporting-issues)

---

## Project Setup

1. Clone the repo:
```sh
   git clone https://github.com/SAARKSensei/Backend_Aws.git
   cd Backend_Aws/
```

2. We use a `Makefile` to simplify common development commands. You can view all available commands by running:
```sh
   make help
```

### Available Makefile Commands:

**Java / Maven:**
- `make compile` - Compile the Java source code
- `make test` - Run the unit tests
- `make build` - Clean and package the application into a JAR
- `make run` - Run the Spring Boot application locally on port 9090
- `make clean` - Clean the target directory

**Docker:**
- `make docker-build` - Build the Docker image locally
- `make docker-up` - Spin up local Docker infrastructure (like Dozzle for logs)
- `make docker-down` - Tear down the Docker infrastructure
- `make docker-logs` - Tail the logs from Docker containers

*Example workflow:*
```sh
# 1. Start your database and logging containers
make docker-up

# 2. Run your application locally
make run
```
Test on http://localhost:9090/api/.....

---

## How to Contribute

1. Fork the repository.
2. Create a new branch from `main`:

```sh
   git checkout -b your-branch-name
```

3. Sync your fork regularly:
```shell
    git remote add upstream https://github.com/SAARKSensei/Backend_Aws.git
    git fetch upstream
    git checkout your-branch-name
    git merge upstream/main
```

4. Make your changes and commit them to your branch.
5. Push to your fork:

```sh
   git push origin your-branch-name
```

6. Compare and Open a Pull Request on GitHub.\
Go to the original repo: 
- URL: `https://github.com/SAARKSensei/Backend_Aws`
- Click `Contribute` → `Open pull request` \
**Then: Check all fields are same as below**
- Base repository: `SAARKSensei/sensei-app-backend`
- Base branch: **`test`** (Make sure this branch is `test` for testing is done before server push. If not, please select it from dropdown.)
- Head repository: `your-username/your-forked-repo-name`
- Compare: `your-branch`
- Submit the PR with a clear title and description.

Again sync your fork regularly.

---

## Coding Guidelines

- Use Java 11 (or higher if specified).
- Follow standard Spring Boot project structure.
- Keep code modular and well-commented.
- Include unit tests for any new logic.

---

## Git Guidelines

- Write clear and descriptive commit messages.
- Prefer multiple small commits over one large commit.
- Use specific commits for individual files or features:

  ```sh
  git add file.java
  git commit -m "Add validation to user service"
  ```

---

## Reporting Issues

If you find a bug or have a feature request:

- Use the [GitHub Issues](https://github.com/SAARKSensei/Backend_Aws/issues) tab.
- Include as much detail as possible (logs, screenshots, steps to reproduce).

---

Thanks again for your interest in contributing to the Sensei backend! 🙌

