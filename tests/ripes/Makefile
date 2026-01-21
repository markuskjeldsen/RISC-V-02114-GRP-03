OUTDIR=.
CC=riscv64-unknown-elf

SRCS:=$(wildcard *.s)
OBJ:=$(SRCS:%.s=$(OUTDIR)/%.out)
BINS:=$(OBJ:%.out=%.bin)
# Create a list of hex files based on the object files
HEXS:=$(OBJ:%.out=%.hex)

# Add HEXS to the default target
all: ${OBJ} ${BINS} ${HEXS}

$(OUTDIR)/%.out: %.s
	$(CC)-gcc -march=rv32i -mabi=ilp32 -ffreestanding -nostartfiles -nostdlib -nodefaultlibs -Wl,-T,linker.ld -o $@ $<

$(OUTDIR)/%.bin: $(OUTDIR)/%.out
	$(CC)-objcopy -O binary $< $@

# Rule to generate the hex file from the binary
# -v: display all input data (don't collapse duplicate lines)
# -e '1/4 "%08x" "\n"': Read 1 unit of 4 bytes, print as 8-digit hex, followed by newline
$(OUTDIR)/%.hex: $(OUTDIR)/%.bin
	hexdump -v -e '1/4 "%08x" "\n"' $< > $@

clean:
	@rm $(OUTDIR)/*.bin $(OUTDIR)/*.out $(OUTDIR)/*.hex
