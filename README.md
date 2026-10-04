# SysMood – System Mood Analyzer

SysMood is a Java Swing desktop application that monitors system CPU and RAM usage and provides a simple system health status based on resource utilization.

## Features

- Monitors CPU usage
- Monitors RAM usage
- Classifies system health as Healthy, Stressed, or Unstable
- Provides system usage suggestions
- Detects active user applications
- Allows users to terminate selected running applications
- Interactive Java Swing graphical user interface

## Technologies Used

- Java
- Java Swing
- AWT
- OperatingSystemMXBean
- PowerShell

## How It Works

The application retrieves CPU and RAM utilization from the operating system using Java's OperatingSystemMXBean.

Based on the resource utilization, the application classifies the system condition:

- Healthy – Low CPU and RAM usage
- Stressed – Moderate resource usage
- Unstable – High resource usage

The application also uses PowerShell to identify active applications with visible windows and displays them in the interface.

## Project Interface

![SysMood Screenshot](sysmood-screenshot.png)

## Project Purpose

The goal of SysMood is to provide a simple and user-friendly desktop interface for monitoring system resources and managing active applications.
