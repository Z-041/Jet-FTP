package com.ftp.protocol;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommandParser 测试")
class CommandParserTest {

    @Nested
    @DisplayName("基本解析测试")
    class BasicParsingTests {

        @Test
        @DisplayName("解析简单命令")
        void parseSimpleCommand() {
            CommandParser parser = new CommandParser("USER");
            assertEquals("USER", parser.getCommand());
            assertEquals("", parser.getArgument());
        }

        @Test
        @DisplayName("解析带参数命令")
        void parseCommandWithArgument() {
            CommandParser parser = new CommandParser("USER john");
            assertEquals("USER", parser.getCommand());
            assertEquals("john", parser.getArgument());
        }

        @Test
        @DisplayName("命令应转换为大写")
        void commandShouldBeUppercase() {
            CommandParser parser = new CommandParser("user john");
            assertEquals("USER", parser.getCommand());
        }

        @Test
        @DisplayName("多空格应被正确处理")
        void multipleSpacesShouldBeHandled() {
            CommandParser parser = new CommandParser("USER    john    ");
            assertEquals("USER", parser.getCommand());
            assertEquals("john", parser.getArgument());
        }
    }

    @Nested
    @DisplayName("带引号的参数解析测试")
    class QuotedArgumentTests {

        @Test
        @DisplayName("解析带引号路径")
        void parseQuotedPath() {
            CommandParser parser = new CommandParser("MKD \"my folder\"");
            assertEquals("MKD", parser.getCommand());
            assertEquals("\"my folder\"", parser.getArgument());
        }

        @Test
        @DisplayName("解析带空格的参数")
        void parseArgumentWithSpaces() {
            CommandParser parser = new CommandParser("RNFR \"path with spaces/file\"");
            assertEquals("RNFR", parser.getCommand());
            assertEquals("\"path with spaces/file\"", parser.getArgument());
        }
    }

    @Nested
    @DisplayName("边界情况测试")
    class EdgeCaseTests {

        @Test
        @DisplayName("空字符串应返回空命令")
        void emptyStringShouldReturnEmptyCommand() {
            CommandParser parser = new CommandParser("");
            assertEquals("", parser.getCommand());
            assertEquals("", parser.getArgument());
        }

        @Test
        @DisplayName("纯空白应被裁剪")
        void whitespaceOnlyShouldBeTrimmed() {
            CommandParser parser = new CommandParser("   ");
            assertEquals("", parser.getCommand());
        }

        @Test
        @DisplayName("仅有空格和命令")
        void onlySpacesAndCommand() {
            CommandParser parser = new CommandParser("   NOOP   ");
            assertEquals("NOOP", parser.getCommand());
            assertEquals("", parser.getArgument());
        }
    }

    @Nested
    @DisplayName("hasArgument 测试")
    class HasArgumentTests {

        @Test
        @DisplayName("有参数时应返回true")
        void withArgumentShouldReturnTrue() {
            CommandParser parser = new CommandParser("USER john");
            assertTrue(parser.hasArgument());
        }

        @Test
        @DisplayName("无参数时应返回false")
        void withoutArgumentShouldReturnFalse() {
            CommandParser parser = new CommandParser("NOOP");
            assertFalse(parser.hasArgument());
        }

        @Test
        @DisplayName("空参数应返回false")
        void emptyArgumentShouldReturnFalse() {
            CommandParser parser = new CommandParser("USER ");
            assertFalse(parser.hasArgument());
        }
    }

    @Nested
    @DisplayName("FTP 命令解析测试")
    class FtpCommandParsingTests {

        @Test
        @DisplayName("USER 命令解析")
        void userCommandParsing() {
            CommandParser parser = new CommandParser("USER anonymous");
            assertEquals("USER", parser.getCommand());
            assertEquals("anonymous", parser.getArgument());
        }

        @Test
        @DisplayName("PASS 命令解析")
        void passCommandParsing() {
            CommandParser parser = new CommandParser("PASS mypassword");
            assertEquals("PASS", parser.getCommand());
            assertEquals("mypassword", parser.getArgument());
        }

        @Test
        @DisplayName("CWD 命令解析")
        void cwdCommandParsing() {
            CommandParser parser = new CommandParser("CWD /public_html");
            assertEquals("CWD", parser.getCommand());
            assertEquals("/public_html", parser.getArgument());
        }

        @Test
        @DisplayName("RETR 命令解析")
        void retrCommandParsing() {
            CommandParser parser = new CommandParser("RETR file.txt");
            assertEquals("RETR", parser.getCommand());
            assertEquals("file.txt", parser.getArgument());
        }

        @Test
        @DisplayName("PORT 命令解析")
        void portCommandParsing() {
            CommandParser parser = new CommandParser("PORT 192,168,1,100,4,1");
            assertEquals("PORT", parser.getCommand());
            assertEquals("192,168,1,100,4,1", parser.getArgument());
        }

        @Test
        @DisplayName("PASV 命令无参数")
        void pasvCommandNoArgument() {
            CommandParser parser = new CommandParser("PASV");
            assertEquals("PASV", parser.getCommand());
            assertFalse(parser.hasArgument());
        }

        @Test
        @DisplayName("LIST 命令可选参数")
        void listCommandOptionalArgument() {
            CommandParser parser1 = new CommandParser("LIST");
            assertEquals("LIST", parser1.getCommand());
            assertFalse(parser1.hasArgument());

            CommandParser parser2 = new CommandParser("LIST -la");
            assertEquals("LIST", parser2.getCommand());
            assertEquals("-la", parser2.getArgument());
        }

        @Test
        @DisplayName("TYPE 命令解析")
        void typeCommandParsing() {
            CommandParser parser = new CommandParser("TYPE I");
            assertEquals("TYPE", parser.getCommand());
            assertEquals("I", parser.getArgument());
        }

        @Test
        @DisplayName("DELE 命令解析")
        void deleCommandParsing() {
            CommandParser parser = new CommandParser("DELE oldfile.txt");
            assertEquals("DELE", parser.getCommand());
            assertEquals("oldfile.txt", parser.getArgument());
        }
    }
}
