# Student Scheduler
![logo](https://github.com/salkhon/StudentScheduler/blob/master/icon/logo.png)

Simple scheduling application that manages your busy schedule.

<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
  </ol>
</details>

## About The Project
![demo](https://github.com/salkhon/StudentScheduler/blob/master/icon/demo.png)

Schedules are necessary for efficient utilization of one's time. This project implements a basic scheduling application capable of doing just that, with simplicity. 

You can create new schedules, assign them a particular time and add notes to remind you of the task at hand. You can also edit and delete already created schedules.

## Built With
This is a java application with the following dependencies:
* [JDK 11](https://www.oracle.com/java/technologies/javase/jdk11-archive-downloads.html)
* [JavaFX 11](https://openjfx.io/)
* [sqlite-jdbc-3.30.1](https://www.sqlitetutorial.net/sqlite-java/sqlite-jdbc-driver/)

## Getting Started
To run the application you have to have the dependencies installed and set up in your IDE.
### Installation
1. Clone the repo
```
git clone git@github.com:salkhon/StudentScheduler.git
```
2. Create a sqlite database file anywhere in your machine.
3. Set up the connection string of that .db file with the scheduler. Navigate to 
`\Student Scheduler\src\com\studsched\model\DatabaseManager.java` and set the `CONNECTION_STRING` variable with this format: `jdbc:sqlite:<PATH TO YOUR .db FILE>`.
4. Now run `\Student Scheduler\src\com\studsched\model\Main.java`.