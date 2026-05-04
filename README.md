# Mini Git-- Geet: Version Control System

A simplified version control system implemented from scratch in Java, 
based on UC Berkeley's Gitlet specification with extensions.

## Implemented Commands
- `init` - initialize repository
- `add` - stage files
- `commit` - create snapshots
- `log` - traverse commit history
- `remove` - unstage / remove files
- `reset` - reset to specific commit
- `push`, `pull` - synchronize with remote

## Design
- Object-oriented architecture: Commit, Blob, Reference classes
- Content-addressed storage with hash-based file indexing
- Disk persistence via Java serialization
- Remote synchronization protocol

## Why I built this
Personal course project of Java Programming when 11/2022, when I just started to learn about coding.  
Understanding how Git works internally — Merkle DAGs, content addressing, 
persistent data structures — by implementing a working version myself.
