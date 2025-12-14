#!/bin/bash

echo "========================================="
echo "Trade Application - Clean Start Script"
echo "========================================="
echo ""

# Set Java path
echo "🔧 Setting Java path..."
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Check Java version
echo "📋 Java version:"
java -version
echo ""

# Ask user if they want to delete the database
read -p "🗑️  Delete existing database for fresh start? (y/N): " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "Deleting database files..."
    rm -f WebAppDataBase.db
    rm -f WebAppDataBase.sql
    echo "✅ Database files deleted"
else
    echo "⏭️  Keeping existing database"
fi
echo ""

# Clean and compile
echo "🔨 Cleaning and compiling project..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Compilation failed!"
    exit 1
fi

echo ""
echo "✅ Compilation successful!"
echo ""

# Ask if user wants to run tests
read -p "🧪 Run tests before starting? (y/N): " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]
then
    echo "Running tests..."
    mvn test
    echo ""
fi

# Start the application
echo "========================================="
echo "🚀 Starting Trade Web Application..."
echo "========================================="
echo ""
echo "📱 Open your browser to: http://localhost:8080"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

mvn exec:java -Dexec.mainClass="co.za.Main.WebTradeApplication.WebApp"