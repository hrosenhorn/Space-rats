/**
 *                   Event scheduler (based on alarm from Vice)
 *                   ------------------------------------------
 *  begin                : Wed May 9 2001
 *  copyright            : (C) 2001 by Simon White
 *  email                : s_a_white@email.com
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 * @author Ken Händel
 *
 */
package libsidplay.common;


/**
 * @author Ken Händel
 * 
 * Private Event Context Object (The scheduler)
 */
public class EventScheduler extends Event implements IEventContext {

	public static final int EVENT_TIMEWARP_COUNT = 0x0FFFFF;

	private Event m_timeWarp;

	private long /* uint */m_events;

	private long /* uint */m_events_future;

	/**
	 * Used to prevent overflowing by timewarping the event clocks
	 */
	public void event() {
		m_events = m_events_future;
		m_events_future = 0;
		m_timeWarp.m_next = this;
		m_timeWarp.m_prev = this.m_prev;
		this.m_prev.m_next = m_timeWarp;
		this.m_prev = m_timeWarp;
		m_timeWarp.m_pending = true;
	}

	/**
	 * Add event to ordered pending queue
	 */
	public void schedule(Event event, long /* event_clock_t */cycles,
			event_phase_t phase) {
		for (;;) {
			if (!event.m_pending) {
				long /* event_clock_t */ clk = m_clk + (cycles << 1);
				clk += ((int)(clk & 1) ^ (phase==event_phase_t.EVENT_CLOCK_PHI1?0:1));
				// Now put in the correct place so we don't need to keep
				// searching the list later.
				Event e;
				long count;
				if (clk >= m_clk) {
					e = m_next;
					count = m_events++;
				} else {
					e = m_timeWarp.m_next;
					count = m_events_future++;
				}
	
				while ((count-- !=0) && (e.m_clk <= clk))
					e = e.m_next;
	
				event.m_next = e;
				event.m_prev = e.m_prev;
				e.m_prev.m_next = event;
				e.m_prev = event;
				event.m_pending = true;
				event.m_clk = clk;
				event.m_context = this;
				break;
			}
			event.cancel();
		}
	}

	public void cancel(Event event) {
		event.m_pending = false;
		event.m_prev.m_next = event.m_next;
		event.m_next.m_prev = event.m_prev;
		m_events--;
	}

	public EventScheduler(final String name) {
		super(name);
		m_timeWarp = new Event("Time Warp") {
		
			public void event() {
				EventScheduler.this.event();
			}
		
		};
		m_events = 0;
		m_events_future = 0;
		m_next = this;
		m_prev = this;
		m_timeWarp.m_clk = 0;
		reset();
	}

	public void reset() {
		// Remove all events
		Event e = m_next;
		// long /* uint */count = m_events;
		m_pending = false;
		while (e.m_pending) {
			e.m_pending = false;
			e = e.m_next;
		}
		m_next = this;
		m_prev = this;
		m_clk  = 0;
		m_events = 0;
		m_events_future = 0;
		event();
	}

	public void clock() {
		// m_clk++;
		// while (m_events && (m_clk >= m_next.m_clk))
		// dispatch (*m_next);

		Event e = m_next;
		m_clk = e.m_clk;
		cancel (e);
		//printf ("Event \"%s\"\n", e.m_name);
		e.event();
	}

	/**
	 * Get time with respect to a specific clock phase
	 */
	public final long /* event_clock_t */getTime(event_phase_t phase) {
		return (m_clk + (((phase == event_phase_t.EVENT_CLOCK_PHI1) ? 0
				: 1) ^ 1)) >> 1;
	}

	public final long /* event_clock_t */getTime(
			long /* event_clock_t */clock, event_phase_t phase) {
		return ((getTime(phase) - clock) << 1) >> 1; // 31 bit res.
	}

	public final event_phase_t phase() {
		return ((m_clk) & 1) == 0 ? event_phase_t.EVENT_CLOCK_PHI1
				: event_phase_t.EVENT_CLOCK_PHI2;
	}

}
