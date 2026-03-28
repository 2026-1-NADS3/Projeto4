#!/bin/bash

echo "===== MONITORAMENTO ====="

echo "CPU:"
top -b -n1 | head -5

echo "Memória:"
free -h

echo "Disco:"
df -h
