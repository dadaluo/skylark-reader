/*
 * Copyright 2015 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.fontbox.ttf

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

/**
 * This class is a version of the one published at
 * https://code.google.com/p/jmzreader/wiki/BufferedRandomAccessFile augmented to handle unsigned
 * bytes. The original class is published under Apache 2.0 license. Fix is marked below
 *
 * This is an optimized version of the RandomAccessFile class as described by Nick Zhang on
 * JavaWorld.com. The article can be found at
 * http://www.javaworld.com/javaworld/javatips/jw-javatip26.html
 *
 * @author jg
 */
class BufferedRandomAccessFile : RandomAccessFile {
    /**
     * Uses a byte instead of a char buffer for efficiency reasons.
     */
    private val buffer: ByteArray
    private var bufend = 0
    private var bufpos = 0

    /**
     * The position inside the actual file.
     */
    private var realpos: Long = 0

    /**
     * Creates a new instance of the BufferedRandomAccessFile.
     *
     * @param filename The path of the file to open.
     * @param mode Specifies the mode to use ("r", "rw", etc.) See the BufferedLineReader
     * documentation for more information.
     * @param bufsize The buffer size (in bytes) to use.
     * @throws FileNotFoundException If the mode is "r" but the given string does not denote an
     * existing regular file, or if the mode begins with "rw" but the given string does not denote
     * an existing, writable regular file and a new regular file of that name cannot be created, or
     * if some other error occurs while opening or creating the file.
     */
    constructor(filename: String?, mode: String, bufsize: Int) : super(filename, mode) {
        buffer = ByteArray(bufsize)
    }

    /**
     * Creates a new instance of the BufferedRandomAccessFile.
     *
     * @param file The file to open.
     * @param mode Specifies the mode to use ("r", "rw", etc.) See the BufferedLineReader
     * documentation for more information.
     * @param bufsize The buffer size (in bytes) to use.
     * @throws FileNotFoundException If the mode is "r" but the given file path does not denote an
     * existing regular file, or if the mode begins with "rw" but the given file path does not denote
     * an existing, writable regular file and a new regular file of that name cannot be created, or
     * if some other error occurs while opening or creating the file.
     */
    constructor(file: File, mode: String, bufsize: Int) : super(file, mode) {
        buffer = ByteArray(bufsize)
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun read(): Int {
        if (bufpos >= bufend && fillBuffer() < 0) {
            return -1
        }
        if (bufend == 0) {
            return -1
        }
        // FIX to handle unsigned bytes
        return (buffer[bufpos++] + 256) and 0xFF
        // End of fix
    }

    /**
     * Reads as much bytes as possible into the internal buffer.
     *
     * @return The total number of bytes read into the buffer, or -1 if there is no more data
     * because the end of the file has been reached.
     * @throws IOException If the first byte cannot be read for any reason other than end of file,
     * or if the random access file has been closed, or if some other I/O error occurs.
     */
    @Throws(IOException::class)
    private fun fillBuffer(): Int {
        val n = super.read(buffer)

        if (n >= 0) {
            realpos += n.toLong()
            bufend = n
            bufpos = 0
        }
        return n
    }

    /**
     * Clears the local buffer.
     *
     * @throws IOException If an I/O error occurs.
     */
    @Throws(IOException::class)
    private fun invalidate() {
        bufend = 0
        bufpos = 0
        realpos = super.getFilePointer()
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun read(b: ByteArray, off: Int, len: Int): Int {
        var curLen = len // length of what is left to read (shrinks)
        var curOff = off // offset where to put read data (grows)
        var totalRead = 0

        while (true) {
            val leftover = bufend - bufpos
            if (curLen <= leftover) {
                System.arraycopy(buffer, bufpos, b, curOff, curLen)
                bufpos += curLen
                return totalRead + curLen
            }
            // curLen > leftover, we need to read more than what remains in buffer
            System.arraycopy(buffer, bufpos, b, curOff, leftover)
            totalRead += leftover
            bufpos += leftover
            if (fillBuffer() > 0) {
                curOff += leftover
                curLen -= leftover
            } else {
                if (totalRead == 0) {
                    return -1
                }
                return totalRead
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun getFilePointer(): Long {
        return realpos - bufend + bufpos
    }

    /**
     * {@inheritDoc}
     */
    @Throws(IOException::class)
    override fun seek(pos: Long) {
        val n = (realpos - pos).toInt()
        if (n >= 0 && n <= bufend) {
            bufpos = bufend - n
        } else {
            super.seek(pos)
            invalidate()
        }
    }
}
