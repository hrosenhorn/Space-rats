/**
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package libsidplay.mem;

public interface IPSIDDrv {
	/**
	 * <PRE>
	 * 	        ; entry address
	 * 	coldvec     .word cold
	 * 
	 * 	        ; initial user interrupt vectors
	 * 	irqusr      .word irqret
	 * 	        ; These should never run
	 * 	brkusr      .word exception
	 * 	nmiusr      .word exception
	 * 
	 * 	        ; redirect basic restart vector
	 * 	        ; to finish the init sequence
	 * 	        ; (hooks in via stop function)
	 * 	stopusr     .word setiomap
	 * 
	 * 	playnum     .byte 0
	 * 	speed       .byte 0
	 * 	initvec     .word 0
	 * 	playvec     .word 0
	 * 	rndwait     .word 0
	 * 	initiomap   .byte 0
	 * 	playiomap   .byte 0
	 * 	video       .byte 0
	 * 	clock       .byte 0
	 * 	flags       .byte 0
	 * 
	 * 	        ; init/play PSID
	 * 	play        jmp (playvec)
	 * 	init        jmp (initvec)
	 * 
	 * 	        ; cold start
	 * 	cold        sei
	 * 
	 * 	        ; setup hardware
	 * 	doinit      ldy $02a6
	 * 	        lda video
	 * 	        sta $02a6
	 * 	        pha
	 * 	        jsr $ff84
	 * 	        pla
	 * 	        sty $02a6
	 * 
	 * 	        ; set VICII raster to line 311 for RSIDs
	 * 	        ldx #$9b
	 * 	        ldy #$37
	 * 
	 * 	        ; we should use the proper values for
	 * 	        ; the default raster, however if the tune
	 * 	        ; is playing at the wrong speed (e.g.
	 * 	        ; PAL at NTSC) use the compatibility
	 * 	        ; raster instead to try make it work
	 * 	        eor clock
	 * 	        ora initiomap
	 * 	        beq vicinit
	 * 
	 * 	        ; set VICII raster to line 0 for PSIDs
	 * 	        ; (compatibility raster)
	 * 	        ldx #$1b
	 * 	        ldy #$00
	 * 	vicinit     stx $d011
	 * 	        sty $d012
	 * 
	 * 	        ; Don't override default irq handler for RSIDs
	 * 	        lda initiomap
	 * 	        beq random
	 * 
	 * 	        ; If play address, override default irq vector so
	 * 	        ; we reach are routine to handle play routine
	 * 	        lda playiomap
	 * 	        beq random
	 * 	        ldx #&lt;irqjob
	 * 	        stx $0314
	 * 
	 * 	        ; simulate time before user loads tune
	 * 	random      ldx rndwait
	 * 	        ldy rndwait+1
	 * 	        inx
	 * 	        iny
	 * 	wait        dex
	 * 	        bne wait
	 * 	        dey
	 * 	        bne wait
	 * 
	 * 	        ; 0 indicates VIC timing (PSIDs only)
	 * 	        ; else it's from CIA
	 * 	        lda speed
	 * 	        bne ciaclear
	 * 
	 * 	        ; disable CIA 1 timer A interrupt but
	 * 	        ; leave timer running for random numbers
	 * 	        lda #$7f
	 * 	        sta $dc0d
	 * 
	 * 	        ; clear any pending irqs
	 * 	        lda $d019
	 * 	        sta $d019
	 * 
	 * 	        ; enable VICII raster interrupt
	 * 	        lda #$81
	 * 	        sta $d01a
	 * 
	 * 	        ; clear any pending irqs
	 * 	ciaclear    lda $dc0d
	 * 
	 * 	        ; set I/O map and call song init routine
	 * 	        lda initiomap
	 * 	        bne setbank
	 * 	        ; Only release interrupt mask for real
	 * 	        ; C64 tunes (initiomap = 0) thus
	 * 	        ; providing a more realistic environment
	 * 	        lda #$37
	 * 	setbank     sta $01
	 * 
	 * 	setregs     lda flags
	 * 	        pha
	 * 	        lda playnum
	 * 	        plp
	 * 	        jsr init
	 * 	setiomap    lda initiomap
	 * 	        beq idle
	 * 	        lda playiomap
	 * 	        beq run
	 * 	        lda #$37
	 * 	        sta $01
	 * 	run         cli
	 * 	idle        jmp idle
	 * 
	 * 	irqjob      lda $01
	 * 	        pha
	 * 	        lda playiomap
	 * 	        sta $01
	 * 	        lda #0
	 * 	        jsr play
	 * 	        pla
	 * 	        sta $01
	 * 	        dec $d019
	 * 	        lda $dc0d
	 * 	        pla
	 * 	        tay
	 * 	        pla
	 * 	        tax
	 * 	        pla
	 * 	        rti
	 * 
	 * 	        ; IRQ Exit (code from Kernel ROM)
	 * 	        ; This loop through is not needed but is
	 * 	        ; to ensure compatibility with psid64
	 * 	irqret      jmp $ea31
	 * 
	 * 	        ; HLT
	 * 	exception   .byte $02
	 * 
	 * 	.end
	 * </PRE>
	 */
	public static final short[] PSIDDRV = {
		0x01, 0x00, 0x6f, 0x36, 0x35, 0x00, 0x00, 0x00,
		0x00, 0x10, 0xc0, 0x00, 0x00, 0x04, 0x00, 0x00,
		0x00, 0x40, 0x00, 0x00, 0x04, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x1d, 0x10, 0xbc, 0x10, 0xbf,
		0x10, 0xbf, 0x10, 0x8e, 0x10, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
		0x00, 0x00, 0x6c, 0x0e, 0x10, 0x6c, 0x0c, 0x10,
		0x78, 0xac, 0xa6, 0x02, 0xad, 0x14, 0x10, 0x8d,
		0xa6, 0x02, 0x48, 0x20, 0x84, 0xff, 0x68, 0x8c,
		0xa6, 0x02, 0xa2, 0x9b, 0xa0, 0x37, 0x4d, 0x15,
		0x10, 0x0d, 0x12, 0x10, 0xf0, 0x04, 0xa2, 0x1b,
		0xa0, 0x00, 0x8e, 0x11, 0xd0, 0x8c, 0x12, 0xd0,
		0xad, 0x12, 0x10, 0xf0, 0x0a, 0xad, 0x13, 0x10,
		0xf0, 0x05, 0xa2, 0xa0, 0x8e, 0x14, 0x03, 0xae,
		0x10, 0x10, 0xac, 0x11, 0x10, 0xe8, 0xc8, 0xca,
		0xd0, 0xfd, 0x88, 0xd0, 0xfa, 0xad, 0x0b, 0x10,
		0xd0, 0x10, 0xa9, 0x7f, 0x8d, 0x0d, 0xdc, 0xad,
		0x19, 0xd0, 0x8d, 0x19, 0xd0, 0xa9, 0x81, 0x8d,
		0x1a, 0xd0, 0xad, 0x0d, 0xdc, 0xad, 0x12, 0x10,
		0xd0, 0x02, 0xa9, 0x37, 0x85, 0x01, 0xad, 0x16,
		0x10, 0x48, 0xad, 0x0a, 0x10, 0x28, 0x20, 0x1a,
		0x10, 0xad, 0x12, 0x10, 0xf0, 0x0a, 0xad, 0x13,
		0x10, 0xf0, 0x04, 0xa9, 0x37, 0x85, 0x01, 0x58,
		0x4c, 0x9d, 0x10, 0xa5, 0x01, 0x48, 0xad, 0x13,
		0x10, 0x85, 0x01, 0xa9, 0x00, 0x20, 0x17, 0x10,
		0x68, 0x85, 0x01, 0xce, 0x19, 0xd0, 0xad, 0x0d,
		0xdc, 0x68, 0xa8, 0x68, 0xaa, 0x68, 0x40, 0x4c,
		0x31, 0xea, 0x02, 0x00, 0x00, 0x01, 0x82, 0x02,
		0x82, 0x02, 0x82, 0x02, 0x82, 0x02, 0x82, 0x10,
		0x82, 0x03, 0x82, 0x07, 0x82, 0x12, 0x82, 0x03,
		0x82, 0x0f, 0x82, 0x05, 0x82, 0x05, 0x22, 0x05,
		0x82, 0x03, 0x82, 0x0b, 0x82, 0x18, 0x82, 0x09,
		0x82, 0x04, 0x82, 0x04, 0x82, 0x03, 0x82, 0x05,
		0x82, 0x0a, 0x82, 0x06, 0x82, 0x07, 0x82, 0x00,
		0x00, 0x00, 0x00, };
}
